package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.BookStoreMapper;
import com.bookstore.bookinventoryservice.repository.BookStoreRepository;
import com.bookstore.bookinventoryservice.service.BookStoreService;
import com.bookstore.bookstorestarter.enums.Flag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookStoreServiceImpl implements BookStoreService {

    private final BookStoreRepository bookStoreRepository;

    private final BookStoreMapper bookStoreMapper;

    Logger logger = LoggerFactory.getLogger(BookStoreServiceImpl.class);

    public BookStoreServiceImpl(BookStoreRepository bookStoreRepository, BookStoreMapper bookStoreMapper) {
        this.bookStoreRepository = bookStoreRepository;
        this.bookStoreMapper = bookStoreMapper;
    }

    @Override
    public BookStoreDTO createBookStore(BookStoreDTO bookStoreDTO) {
        logger.info("Creating book store detail");
        Optional<BookStore> existingBookStore = bookStoreRepository.findByName(bookStoreDTO.getName());
        if (existingBookStore.isPresent()) {
            throw new RecordNotFoundException("Book Store already exists with name " + bookStoreDTO.getName());
        }
        BookStore bookStore = bookStoreMapper.bookStoreDTOToBookStore(bookStoreDTO);
        bookStore.setFlag(Flag.ENABLED);
        bookStore = bookStoreRepository.save(bookStore);

        return bookStoreMapper.bookStoreToBookStoreDTO(bookStore);
    }

    @Override
    public BookStoreDTO getBookStoreById(Long id) {
        logger.info("Fetch book store detail {}", id);
        BookStore bookStore = bookStoreRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+id));

        return bookStoreMapper.bookStoreToBookStoreDTO(bookStore);
    }

    @Override
    public List<BookStoreDTO> getAllBookStores() {
        logger.info("fetching book stores detail");

        List<BookStore> bookStoreList = bookStoreRepository.findAllByFlag(Flag.ENABLED);
        List<BookStoreDTO> bookStoreDTOList = new ArrayList<>();
        for (BookStore bookStore : bookStoreList) {
            bookStoreDTOList.add(bookStoreMapper.bookStoreToBookStoreDTO(bookStore));
        }

        return bookStoreDTOList;
    }

    @Override
    public BookStoreDTO updateBookStore(BookStoreDTO bookStoreDTO, Long bookId) {
        logger.info("Updating book store detail {}", bookId);
        BookStore bookStore = bookStoreRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+bookId));

        bookStore.setName(bookStoreDTO.getName());
        bookStore.setDescription(bookStoreDTO.getDescription());
        bookStore.setType(bookStoreDTO.getType());
        bookStore.setContactEmail(bookStoreDTO.getContactEmail());
        bookStore.setContactPhone(bookStoreDTO.getContactPhone());

        bookStore.getAddress().setCity(bookStoreDTO.getAddress().getCity());
        bookStore.getAddress().setState(bookStoreDTO.getAddress().getState());
        bookStore.getAddress().setStreet(bookStoreDTO.getAddress().getStreet());
        bookStore.getAddress().setCountry(bookStoreDTO.getAddress().getCountry());
        bookStore.getAddress().setPostalCode(bookStoreDTO.getAddress().getPostalCode());

        bookStore = bookStoreRepository.save(bookStore);

        return bookStoreMapper.bookStoreToBookStoreDTO(bookStore);
    }

    @Override
    public BookStoreDTO deleteBookStore(Long bookId) {
        logger.info("Deleting book store detail {}", bookId);
        BookStore bookStore = bookStoreRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+bookId));

        bookStore.setFlag(Flag.DISABLED);
        bookStore = bookStoreRepository.save(bookStore);

        return bookStoreMapper.bookStoreToBookStoreDTO(bookStore);
    }
}
