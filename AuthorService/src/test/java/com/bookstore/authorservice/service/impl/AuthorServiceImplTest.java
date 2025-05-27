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
        Author author = new Author();
        author.setId(authorId);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("john.doe@gmail.com");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(authorId);
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@gmail.com");

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
        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("john.doe@gmail.com");
        author.setFlag(Flag.ENABLED);

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@gmail.com");
        authorDTO.setFlag(Flag.ENABLED);


        when(authorRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(authorMapper.authorDTOToAuthor(authorDTO)).thenReturn(author);
        when(authorMapper.authorToAuthorDTO(author)).thenReturn(authorDTO);
        when(authorRepository.save(author)).thenReturn(author);

        //when
        AuthorDTO result = authorService.createAuthor(authorDTO);

        //then
        assertEquals(authorDTO.getFirstName(), result.getFirstName());
        assertEquals(authorDTO.getLastName(), result.getLastName());
        assertEquals(result.getFlag(), Flag.ENABLED);

        verify(authorRepository).findByEmail(any());
        verify(authorMapper).authorDTOToAuthor(authorDTO);
        verify(authorMapper).authorToAuthorDTO(author);
        verify(authorRepository).save(author);
    }

    @Test
    void createAuthor_whenAuthorEmailDoesExist_throwsException() {
        //Given
        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("john.doe@gmail.com");
        author.setFlag(Flag.ENABLED);

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@gmail.com");
        authorDTO.setFlag(Flag.ENABLED);


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

        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setFirstName("Old");
        existingAuthor.setLastName("Name");
        existingAuthor.setEmail("old@example.com");
        existingAuthor.setBio("Old bio");
        existingAuthor.setFlag(Flag.ENABLED);

        AuthorDTO updateDTO = new AuthorDTO();
        updateDTO.setFirstName("New");
        updateDTO.setLastName("Name");
        updateDTO.setEmail("new@example.com");
        updateDTO.setBio("Updated bio");
        updateDTO.setFlag(Flag.ENABLED);

        Author updatedAuthor = new Author();
        updatedAuthor.setId(authorId);
        updatedAuthor.setFirstName("New");
        updatedAuthor.setLastName("Name");
        updatedAuthor.setEmail("new@example.com");
        updatedAuthor.setBio("Updated bio");
        updatedAuthor.setFlag(Flag.ENABLED);

        AuthorDTO expectedDTO = new AuthorDTO();
        expectedDTO.setId(authorId);
        expectedDTO.setFirstName("New");
        expectedDTO.setLastName("Name");
        expectedDTO.setEmail("new@example.com");
        expectedDTO.setBio("Updated bio");
        expectedDTO.setFlag(Flag.ENABLED);

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
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("john.doe@gmail.com");
        author.setFlag(Flag.ENABLED);

        Author author1 = new Author();
        author1.setId(2L);
        author1.setFirstName("Johnson");
        author1.setLastName("Does");
        author1.setEmail("johnson.doe@gmail.com");
        author1.setFlag(Flag.ENABLED);

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@gmail.com");
        authorDTO.setFlag(Flag.ENABLED);

        AuthorDTO authorDTO1 = new AuthorDTO();
        authorDTO1.setId(2L);
        authorDTO1.setFirstName("Johnson");
        authorDTO1.setLastName("Does");
        authorDTO1.setEmail("johnson.doe@gmail.com");
        authorDTO1.setFlag(Flag.ENABLED);

        List<Author> authors = List.of(author, author1);
        when(authorRepository.findAllByFlag(Flag.ENABLED)).thenReturn(authors);
        when(authorMapper.authorToAuthorDTO(author)).thenReturn(authorDTO);
        when(authorMapper.authorToAuthorDTO(author1)).thenReturn(authorDTO1);

        //when
        List<AuthorDTO> result = authorService.getAllAuthors();

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(authorDTO.getId(), result.get(0).getId());
        assertEquals(authorDTO, result.get(0));
        assertEquals(authorDTO1, result.get(1));
        assertEquals(authorDTO1, result.get(1));

        verify(authorRepository).findAllByFlag(Flag.ENABLED);
        verify(authorMapper, times(2)).authorToAuthorDTO(any());
    }
}