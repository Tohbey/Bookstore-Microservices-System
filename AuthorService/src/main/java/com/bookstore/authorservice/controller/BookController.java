package com.bookstore.authorservice.controller;

import com.bookstore.authorservice.Util.IDataResponse;
import com.bookstore.authorservice.dtos.PublishDto;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.authorservice.service.BookService;
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
@RequestMapping(BookController.BASE_URL)
public class BookController {
    public static final String BASE_URL = "/api/books";

    private final BookService bookService;

    Logger logger = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public IDataResponse<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        logger.info("Creating book details {}", bookDTO);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.createBook(bookDTO)));
        response.setValid(true);
        response.setMessage("Book created successfully");
        return response;
    }

    @PutMapping(value = "{bookId}")
    public IDataResponse<BookDTO> updateBook(@RequestBody BookDTO bookDTO, @PathVariable Long bookId) {
        logger.info("Updating book details {}", bookId);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.updateBook(bookDTO, bookId)));
        response.setValid(true);
        response.setMessage("Book updated successfully");
        return response;
    }

    @GetMapping(value = "{bookId}")
    public IDataResponse<BookDTO> getBook(@PathVariable Long bookId) {
        logger.info("Retrieving book details for bookId {}", bookId);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.getBookById(bookId)));
        response.setValid(true);
        response.setMessage("Book retrieved successfully");
        return response;
    }

    @PostMapping(value = "all")
    public IDataResponse<BookDTO> getBooks(@RequestBody List<Long> authorIds) {
        logger.info("Retrieving book details");
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(bookService.getAllBooks(authorIds));
        response.setValid(true);
        response.setMessage("Books retrieved successfully");
        return response;
    }

    @PostMapping(value = "publish")
    public IDataResponse<BookDTO> publishBook(@RequestBody PublishDto publishDto) {
        logger.info("Publishing book details");
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.publishBook(publishDto)));
        response.setValid(true);
        response.setMessage("Book published successfully");
        return response;
    }

    @DeleteMapping(value = "{bookId}")
    public IDataResponse<BookDTO> deleteBook(@PathVariable Long bookId) {
        logger.info("deleting book details for bookId {}", bookId);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.deleteBook(bookId)));
        response.setValid(true);
        response.setMessage("Book deleted successfully");
        return response;
    }
}
