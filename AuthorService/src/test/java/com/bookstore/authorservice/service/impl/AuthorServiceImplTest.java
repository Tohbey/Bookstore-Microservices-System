package com.bookstore.authorservice.service.impl;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.exception.RecordAlreadyExistException;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.AuthorDTO;
import com.bookstore.authorservice.mapper.mappers.AuthorMapper;
import com.bookstore.authorservice.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.bookstore.authorservice.mock.MockData.getAuthorDTOs;
import static com.bookstore.authorservice.mock.MockData.getAuthors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAuthorById() {
        //Given
        Long authorId = 1L;
        Author author = getAuthors().get(0);
        AuthorDTO authorDTO = getAuthorDTOs().get(0);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.authorToAuthorDTO(author)).thenReturn(authorDTO);

        //when
        AuthorDTO result = authorService.getAuthorById(authorId);

        //then
        assertNotNull(result);
        assertEquals(authorId, result.getId());
        assertEquals(author.getFirstName(), result.getFirstName());
        assertEquals(author.getLastName(), result.getLastName());
        assertEquals(author.getEmail(), result.getEmail());
        verify(authorRepository).findById(authorId);
        verify(authorMapper).authorToAuthorDTO(author);
    }

    @Test
    void getAuthorById_whenAuthorDoesNotExist_throwsException() {
        //given
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        //when
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class, () -> authorService.getAuthorById(authorId));

        assertNotNull(exception);
        assertEquals("Author Not Found 1", exception.getMessage());
        verify(authorRepository).findById(authorId);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void createAuthor() {
        //Given
        Author author = getAuthors().get(0);
        author.setId(null);
        AuthorDTO authorDTO = getAuthorDTOs().get(0);
        authorDTO.setId(null);

        when(authorRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(authorMapper.authorDTOToAuthor(authorDTO)).thenReturn(author);
        when(authorMapper.authorToAuthorDTO(author)).thenReturn(authorDTO);
        when(authorRepository.save(author)).thenReturn(author);

        //when
        AuthorDTO result = authorService.createAuthor(authorDTO);

        //then
        assertEquals(authorDTO.getFirstName(), result.getFirstName());
        assertEquals(authorDTO.getLastName(), result.getLastName());
        assertEquals(Flag.ENABLED, result.getFlag());

        verify(authorRepository).findByEmail(any());
        verify(authorMapper).authorDTOToAuthor(authorDTO);
        verify(authorMapper).authorToAuthorDTO(author);
        verify(authorRepository).save(author);
    }

    @Test
    void createAuthor_whenAuthorEmailDoesExist_throwsException() {
        //Given
        Author author = getAuthors().get(0);
        AuthorDTO authorDTO = getAuthorDTOs().get(0);

        when(authorRepository.findByEmail(any())).thenReturn(Optional.of(author));

        //when
        RecordAlreadyExistException exception = assertThrows(
                RecordAlreadyExistException.class, () -> authorService.createAuthor(authorDTO));

        //then
        assertNotNull(exception);
        assertEquals("Author already exists", exception.getMessage());

        verify(authorRepository).findByEmail(any());
        verifyNoInteractions(authorMapper);
        verify(authorRepository, never()).save(any());
    }

    @Test
    void updateAuthor_whenAuthorExists_shouldUpdateAndReturnDTO() {
        // Given
        Long authorId = 1L;

        Author existingAuthor = getAuthors().get(0);

        AuthorDTO updateDTO = getAuthorDTOs().get(0);

        Author updatedAuthor = getAuthors().get(0);
        updatedAuthor.setEmail("new@example.com");
        updatedAuthor.setBio("Updated bio");
        updatedAuthor.setFirstName("New");

        AuthorDTO expectedDTO = getAuthorDTOs().get(0);
        expectedDTO.setEmail("new@example.com");
        expectedDTO.setBio("Updated bio");
        expectedDTO.setFirstName("New");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(updatedAuthor);
        when(authorMapper.authorToAuthorDTO(updatedAuthor)).thenReturn(expectedDTO);

        // When
        AuthorDTO result = authorService.updateAuthor(updateDTO, authorId);

        // Then
        assertNotNull(result);
        assertEquals("New", result.getFirstName());
        assertEquals("Updated bio", result.getBio());
        assertEquals("new@example.com", result.getEmail());
        verify(authorRepository).findById(authorId);
        verify(authorRepository).save(any(Author.class));
        verify(authorMapper).authorToAuthorDTO(updatedAuthor);
    }

    @Test
    void updateAuthor_whenAuthorNotFound_shouldThrowException() {
        // Given
        Long authorId = 999L;
        AuthorDTO updateDTO = new AuthorDTO();
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // When & Then
        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () ->
                authorService.updateAuthor(updateDTO, authorId));

        assertEquals("Author Not Found 999", exception.getMessage());
        verify(authorRepository).findById(authorId);
        verify(authorRepository, never()).save(any());
        verifyNoInteractions(authorMapper);
    }
    @Test
    void getAllAuthors() {
        List<Author> authors = getAuthors();
        List<AuthorDTO> authorDTOs = getAuthorDTOs();
        when(authorRepository.findAllByFlag(Flag.ENABLED)).thenReturn(authors);
        when(authorMapper.authorToAuthorDTO(authors.get(0))).thenReturn(authorDTOs.get(0));
        when(authorMapper.authorToAuthorDTO(authors.get(1))).thenReturn(authorDTOs.get(1));

        //when
        List<AuthorDTO> result = authorService.getAllAuthors();

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(authorDTOs.get(0).getId(), result.get(0).getId());
        assertEquals(authorDTOs.get(0), result.get(0));
        assertEquals(authorDTOs.get(1), result.get(1));
        assertEquals(authorDTOs.get(1), result.get(1));

        verify(authorRepository).findAllByFlag(Flag.ENABLED);
        verify(authorMapper, times(2)).authorToAuthorDTO(any());
    }

    @Test
    void deleteAuthor() {
        //Given
        Long authorId = 1L;
        Author author = getAuthors().get(0);
        AuthorDTO authorDTO = getAuthorDTOs().get(0);
        authorDTO.setFlag(Flag.DISABLED);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.authorToAuthorDTO(author)).thenReturn(authorDTO);
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        //when
        AuthorDTO result = authorService.deleteAuthor(authorId);

        //then
        assertNotNull(result);
        assertEquals(authorId, result.getId());
        assertEquals(Flag.DISABLED, result.getFlag());
        verify(authorRepository).findById(authorId);
        verify(authorMapper).authorToAuthorDTO(author);
    }

    @Test
    void deleteAuthor_whenAuthorDoesNotExist_throwsException() {
        //given
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        //when
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class, () -> authorService.deleteAuthor(authorId));

        assertNotNull(exception);
        assertEquals("Author Not Found 1", exception.getMessage());
        verify(authorRepository).findById(authorId);
        verifyNoInteractions(authorMapper);
    }
}