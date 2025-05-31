package com.bookstore.authorservice.service.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
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
    public List<BookDTO> getAllBooks(Flag flag, List<Long> authorIds) {
        logger.info("fetching books {} or {}", flag, authorIds);
        List<Book> books;
        List<BookDTO> bookDTOs = new ArrayList<>();
        if (authorIds == null || authorIds.isEmpty()) {
            books = bookRepository.findAllByFlag(flag);
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
        return null;
    }
}
