package com.bookstore.authorservice.service.impl;

import com.bookstore.authorservice.config.MessageProducer;
import com.bookstore.authorservice.dtos.PublishDto;
import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.entity.Book;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.enums.Status;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.AuthorDTO;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.authorservice.mapper.mappers.BookMapper;
import com.bookstore.authorservice.repository.AuthorRepository;
import com.bookstore.authorservice.repository.BookRepository;
import com.bookstore.authorservice.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bookstore.authorservice.config.KafkaTopics.BOOK_PUBLISHED;

@Service
public class BookServiceImpl implements BookService {

    Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final BookMapper bookMapper;

    private final MessageProducer messageProducer;

    private ObjectMapper objectMapper;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, BookMapper bookMapper, MessageProducer messageProducer) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
        this.messageProducer = messageProducer;
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        logger.info("Creating book");

        List<Author> authors = fetchAuthorsFromDTO(bookDTO.getAuthors());

        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setGenre(bookDTO.getGenre());
        book.setSynopsis(bookDTO.getSynopsis());
        book.setStatus(bookDTO.getStatus() != null ? bookDTO.getStatus() : Status.DRAFT);
        book.setAuthors(authors);
        Book saved = bookRepository.save(book);

        return bookMapper.bookToBookDTO(saved);
    }

    @Override
    public BookDTO updateBook(BookDTO bookDTO, Long bookId) {
        logger.info("update book details {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book Not Found: " + bookId));

        book.setTitle(bookDTO.getTitle());
        book.setGenre(bookDTO.getGenre());
        book.setSynopsis(bookDTO.getSynopsis());
        if (bookDTO.getStatus() != null && bookDTO.getStatus() != Status.PUBLISHED) {
            book.setStatus(bookDTO.getStatus());
        }

        List<Author> authors = fetchAuthorsFromDTO(bookDTO.getAuthors());
        book.setAuthors(authors);

        Book updated = bookRepository.save(book);

        return bookMapper.bookToBookDTO(updated);
    }

    private List<Author> fetchAuthorsFromDTO(List<AuthorDTO> authorDTOs) {
        if (authorDTOs == null || authorDTOs.isEmpty()) {
            throw new IllegalArgumentException("Author list cannot be empty");
        }

        List<Long> authorIds = authorDTOs.stream()
                .map(AuthorDTO::getId)
                .collect(Collectors.toList());

        List<Author> authors = authorRepository.findAllByIdIn(authorIds);

        if (authors.size() != authorIds.size()) {
            throw new RecordNotFoundException("Some authors were not found");
        }

        return authors;
    }

    @Override
    public List<BookDTO> getAllBooks(List<Long> authorIds) {
        logger.info("fetching books by {}", authorIds);
        List<Book> books;
        List<BookDTO> bookDTOs = new ArrayList<>();
        if (authorIds == null || authorIds.isEmpty()) {
            books = bookRepository.findAllByFlag(Flag.ENABLED);
        }else{
            List<Author> authors = authorRepository.findAllByIdIn(authorIds);
            books = bookRepository.findAllByAuthorsAndFlag(authors, Flag.ENABLED);
        }

        books.forEach(book -> {
           bookDTOs.add(bookMapper.bookToBookDTO(book));
        });

        return bookDTOs;
    }

    @Override
    public BookDTO getBookById(Long bookId) {
        logger.info("Getting book details {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book Not Found "+bookId));

        return bookMapper.bookToBookDTO(book);
    }

    @Override
    public BookDTO publishBook(PublishDto publishDto) {
        try{
            logger.info("Publishing book");

            Book book = bookRepository.findById(publishDto.getBookDTO().getId())
                    .orElseThrow(() -> new RecordNotFoundException("Book Not Found "+publishDto.getBookDTO().getId()));

            if(publishDto.getPublishedCopies() > book.getTotalCopies()){
                throw new RuntimeException("Not enough copies available");
            }

            book.setStatus(Status.PUBLISHED);
            book.setPublishedAt(LocalDateTime.now());
            book.setTotalCopies(book.getTotalCopies() - publishDto.getPublishedCopies());
            Book saved = bookRepository.save(book);

            BookDTO bookDTO = bookMapper.bookToBookDTO(saved);
            publishDto.setBookDTO(bookDTO);
            publishDto.setRemainingCopies(bookDTO.getTotalCopies() - publishDto.getPublishedCopies());

            objectMapper.registerModule(new JavaTimeModule());
            String payload = objectMapper.writeValueAsString(publishDto);
            logger.info("Published book: {}", payload);

            messageProducer.sendMessage(BOOK_PUBLISHED, payload);

            return bookDTO;
        }catch (JsonProcessingException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BookDTO deleteBook(Long bookId) {
        logger.info("Deleting book details {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book Not Found "+bookId));

        book.setFlag(Flag.DISABLED);

        book = bookRepository.save(book);

        return bookMapper.bookToBookDTO(book);
    }
}
