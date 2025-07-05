package com.bookstore.bookinventoryservice.controller;

import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import com.bookstore.bookinventoryservice.service.BookStoreService;
import com.bookstore.bookstorestarter.Util.IDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(BookStoreController.BASE_URL)
public class BookStoreController {
    public static final String BASE_URL = "/api/book-store";

    private final BookStoreService bookStoreService;

    Logger logger = LoggerFactory.getLogger(BookStoreController.class);

    public BookStoreController(BookStoreService bookStoreService) {
        this.bookStoreService = bookStoreService;
    }

    @PostMapping
    public IDataResponse<BookStoreDTO> createBookStore(@RequestBody BookStoreDTO bookStoreDTO) {
        logger.info("Creating new book store {}", bookStoreDTO);
        IDataResponse<BookStoreDTO> response = new IDataResponse<>();
        response.setData(List.of(bookStoreService.createBookStore(bookStoreDTO)));
        response.setValid(true);
        response.setMessage("Book Store created successfully");
        return response;
    }

    @PutMapping(value = "{bookStoreId}")
    public IDataResponse<BookStoreDTO> updateBookStore(@RequestBody BookStoreDTO bookStoreDTO, @PathVariable Long bookStoreId) {
        logger.info("Updating book store details {}", bookStoreDTO);
        IDataResponse<BookStoreDTO> response = new IDataResponse<>();
        response.setData(List.of(bookStoreService.updateBookStore(bookStoreDTO, bookStoreId)));
        response.setValid(true);
        response.setMessage("Book Store updated successfully");
        return response;
    }

    @GetMapping(value = "{bookStoreId}")
    public IDataResponse<BookStoreDTO> getBookStoreById(@PathVariable Long bookStoreId) {
        logger.info("Getting book store details {}", bookStoreId);
        IDataResponse<BookStoreDTO> response = new IDataResponse<>();
        response.setData(List.of(bookStoreService.getBookStoreById(bookStoreId)));
        response.setValid(true);
        response.setMessage("Book Store retrieved successfully");
        return response;
    }

    @GetMapping()
    public IDataResponse<BookStoreDTO> getAllBookStores() {
        logger.info("Getting book stores details");
        IDataResponse<BookStoreDTO> response = new IDataResponse<>();
        response.setData(bookStoreService.getAllBookStores());
        response.setValid(true);
        response.setMessage("Book stores retrieved successfully");
        return response;
    }

    @DeleteMapping(value = "{bookStoreId}")
    public IDataResponse<BookStoreDTO> deleteBookStoreById(@PathVariable Long bookStoreId) {
        logger.info("Deleting book store details {}", bookStoreId);
        IDataResponse<BookStoreDTO> response = new IDataResponse<>();
        response.setData(List.of(bookStoreService.deleteBookStore(bookStoreId)));
        response.setValid(true);
        response.setMessage("Book Store deleted successfully");
        return response;
    }

}
