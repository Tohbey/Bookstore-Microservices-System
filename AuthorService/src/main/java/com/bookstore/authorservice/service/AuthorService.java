package com.bookstore.authorservice.service;

import com.bookstore.authorservice.mapper.dtos.AuthorDTO;

import java.util.List;

public interface AuthorService {
    AuthorDTO getAuthorById(Long id);

    AuthorDTO createAuthor(AuthorDTO authorDTO);

    AuthorDTO updateAuthor(AuthorDTO authorDTO, Long authorId);

    List<AuthorDTO> getAllAuthors();

}
