package com.bookstore.authorservice.controller;

import com.bookstore.authorservice.Util.IDataResponse;
import com.bookstore.authorservice.dtos.PublishDto;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.authorservice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Book Management", description = "APIs for managing books")
public class BookController {
    public static final String BASE_URL = "/api/books";

    private final BookService bookService;

    Logger logger = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Add a new book to the system")
    public IDataResponse<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        logger.info("Creating book details {}", bookDTO);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.createBook(bookDTO)));
        response.setValid(true);
        response.setMessage("Book created successfully");
        return response;
    }

    @PutMapping(value = "{bookId}")
    @Operation(summary = "Update a book", description = "Update an existing book's details")
    public IDataResponse<BookDTO> updateBook(@RequestBody BookDTO bookDTO, @PathVariable Long bookId) {
        logger.info("Updating book details {}", bookId);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.updateBook(bookDTO, bookId)));
        response.setValid(true);
        response.setMessage("Book updated successfully");
        return response;
    }

    @GetMapping(value = "{bookId}")
    @Operation(summary = "Get book by ID", description = "Retrieve a book's details using their ID")
    public IDataResponse<BookDTO> getBook(@PathVariable Long bookId) {
        logger.info("Retrieving book details for bookId {}", bookId);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.getBookById(bookId)));
        response.setValid(true);
        response.setMessage("Book retrieved successfully");
        return response;
    }

    @PostMapping(value = "all")
    @Operation(summary = "Get all books", description = "Retrieve a list of all books in the system")
    public IDataResponse<BookDTO> getBooks(@RequestBody List<Long> authorIds) {
        logger.info("Retrieving book details");
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(bookService.getAllBooks(authorIds));
        response.setValid(true);
        response.setMessage("Books retrieved successfully");
        return response;
    }

    @PostMapping(value = "publish")
    @Operation(summary = "Publish a book", description = "Publish book for distributors")
    public IDataResponse<BookDTO> publishBook(@RequestBody PublishDto publishDto) {
        logger.info("Publishing book details");
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.publishBook(publishDto)));
        response.setValid(true);
        response.setMessage("Book published successfully");
        return response;
    }

    @DeleteMapping(value = "{bookId}")
    @Operation(summary = "Delete a book", description = "Delete a book from the system using their ID")
    public IDataResponse<BookDTO> deleteBook(@PathVariable Long bookId) {
        logger.info("deleting book details for bookId {}", bookId);
        IDataResponse<BookDTO> response = new IDataResponse<>();
        response.setData(List.of(bookService.deleteBook(bookId)));
        response.setValid(true);
        response.setMessage("Book deleted successfully");
        return response;
    }
}
