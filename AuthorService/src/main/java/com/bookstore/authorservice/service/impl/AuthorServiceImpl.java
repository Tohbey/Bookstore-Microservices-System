package com.bookstore.authorservice.service.impl;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.exception.RecordAlreadyExistException;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.AuthorDTO;
import com.bookstore.authorservice.mapper.mappers.AuthorMapper;
import com.bookstore.authorservice.repository.AuthorRepository;
import com.bookstore.authorservice.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public AuthorDTO getAuthorById(Long id) {
        logger.info("Getting author details {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author Not Found "+id));

        return authorMapper.authorToAuthorDTO(author);
    }

    @Override
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        logger.info("Creating author");

        Optional<Author> findExistingAuthor = authorRepository.findByEmail(authorDTO.getEmail());
        if (findExistingAuthor.isPresent()) {
            throw new RecordAlreadyExistException("Author already exists");
        }

        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        author = authorRepository.save(author);

        logger.info("Author created");

        return authorMapper.authorToAuthorDTO(author);
    }

    @Override
    public AuthorDTO updateAuthor(AuthorDTO authorDTO, Long authorId) {
        logger.info("update author details {}", authorId);

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RecordNotFoundException("Author Not Found "+authorId));

        author.setFirstName(authorDTO.getFirstName());
        author.setLastName(authorDTO.getLastName());
        author.setEmail(authorDTO.getEmail());
        author.setBio(authorDTO.getBio());
        author.setFlag(authorDTO.getFlag());
        author = authorRepository.save(author);

        return authorMapper.authorToAuthorDTO(author);
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        logger.info("get author details");
        List<Author> authors = authorRepository.findAllByFlag(Flag.ENABLED);
        List<AuthorDTO> authorDTOs = new ArrayList<>();
        for (Author author : authors) {
            authorDTOs.add(authorMapper.authorToAuthorDTO(author));
        }

        return authorDTOs;
    }

    @Override
    public AuthorDTO deleteAuthor(Long authorId) {
        logger.info("deleting author details {}", authorId);

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RecordNotFoundException("Author Not Found "+authorId));

        author.setFlag(Flag.DISABLED);

        author = authorRepository.save(author);

        return authorMapper.authorToAuthorDTO(author);
    }
}
