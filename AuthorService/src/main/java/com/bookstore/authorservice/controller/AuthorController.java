package com.bookstore.authorservice.controller;

import com.bookstore.authorservice.Util.IDataResponse;
import com.bookstore.authorservice.mapper.dtos.AuthorDTO;
import com.bookstore.authorservice.service.AuthorService;
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
@RequestMapping(AuthorController.BASE_URL)
public class AuthorController {
    public static final String BASE_URL = "/api/authors";

    private final AuthorService authorService;

    Logger logger = LoggerFactory.getLogger(AuthorController.class);

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public IDataResponse<AuthorDTO> createAuthor(@RequestBody AuthorDTO authorDTO) {
        logger.info("Creating author details {}", authorDTO);
        IDataResponse<AuthorDTO> response = new IDataResponse<>();
        response.setData(List.of(authorService.createAuthor(authorDTO)));
        response.setValid(true);
        response.setMessage("Author created successfully");
        return response;
    }

    @PutMapping(value = "{authorId}")
    public IDataResponse<AuthorDTO> updateAuthor(@RequestBody AuthorDTO authorDTO
            , @PathVariable Long authorId) {
        logger.info("Updating author details {}", authorId);
        IDataResponse<AuthorDTO> response = new IDataResponse<>();
        response.setData(List.of(authorService.updateAuthor(authorDTO, authorId)));
        response.setValid(true);
        response.setMessage("Author updated successfully");
        return response;
    }

    @GetMapping(value = "{authorId}")
    public IDataResponse<AuthorDTO> getAuthorById(@PathVariable Long authorId) {
        logger.info("Getting author details {}", authorId);
        IDataResponse<AuthorDTO> response = new IDataResponse<>();
        response.setData(List.of(authorService.getAuthorById(authorId)));
        response.setValid(true);
        response.setMessage("Author retrieved successfully");
        return response;
    }

    @GetMapping()
    public IDataResponse<AuthorDTO> getAuthors() {
        logger.info("Getting authors details");
        IDataResponse<AuthorDTO> response = new IDataResponse<>();
        response.setData(authorService.getAllAuthors());
        response.setValid(true);
        response.setMessage("Authors retrieved successfully");
        return response;
    }

    @DeleteMapping(value = "{authorId}")
    public IDataResponse<AuthorDTO> deleteAuthorById(@PathVariable Long authorId) {
        logger.info("Deleting author details {}", authorId);
        IDataResponse<AuthorDTO> response = new IDataResponse<>();
        response.setData(List.of(authorService.deleteAuthor(authorId)));
        response.setValid(true);
        response.setMessage("Author deleted successfully");
        return response;
    }
}
