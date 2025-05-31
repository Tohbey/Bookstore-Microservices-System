package com.bookstore.authorservice.service.impl;

import com.bookstore.authorservice.entity.Book;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.enums.Status;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.authorservice.mapper.mappers.AuthorMapper;
import com.bookstore.authorservice.mapper.mappers.BookMapper;
import com.bookstore.authorservice.repository.AuthorRepository;
import com.bookstore.authorservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bookstore.authorservice.mock.MockData.getAuthorDTOs;
import static com.bookstore.authorservice.mock.MockData.getAuthors;
import static com.bookstore.authorservice.mock.MockData.getBooks;
import static com.bookstore.authorservice.mock.MockData.getBookDTOs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private AuthorMapper authorMapper;

    Long authorId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook() {
        // Given
        Book book = getBooks().get(0);
        BookDTO bookDTO = getBookDTOs().get(0);

        when(authorRepository.findAllByIdIn(anyList())).thenReturn(List.of(getAuthors().get(0)));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);

        //when
        BookDTO createdBookDTO = bookService.createBook(bookDTO);

        //then
        assertNotNull(createdBookDTO);
        assertEquals(bookDTO.getId(), createdBookDTO.getId());
        assertEquals(1, bookDTO.getAuthors().size());
        assertEquals(getAuthorDTOs().get(0).getId(), bookDTO.getAuthors().get(0).getId());
    }

    @Test
    void createBook_throwsExceptionWhenAuthorDTOIsEmpty() {
        // Given
        BookDTO bookDTO = getBookDTOs().get(0);
        bookDTO.setAuthors(null);

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> bookService.createBook(bookDTO));

        //then
        assertNotNull(exception);
        assertEquals("Author list cannot be empty", exception.getMessage());
    }

    @Test
    void createBook_throwsRecordNotFoundWhenAuthorIsEmpty() {
        // Given
        BookDTO bookDTO = getBookDTOs().get(0);

        when(authorRepository.findAllByIdIn(anyList())).thenReturn(List.of());

        //when
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class, () -> bookService.createBook(bookDTO));

        //then
        assertNotNull(exception);
        assertEquals("Some authors were not found", exception.getMessage());
    }


    @Test
    void updateBook() {
        // Given
        Long bookId = 1L;
        Book book = getBooks().get(0);
        BookDTO bookDTO = getBookDTOs().get(0);
        bookDTO.setStatus(Status.PUBLISHED);
        LocalDateTime publishedDate = LocalDateTime.now();
        bookDTO.setPublishedAt(publishedDate);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findAllByIdIn(anyList())).thenReturn(List.of(getAuthors().get(0)));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);

        //when
        BookDTO updateBook = bookService.updateBook(bookDTO, bookId);

        //then
        assertNotNull(updateBook);
        assertEquals(bookDTO.getId(), updateBook.getId());
        assertEquals(Status.PUBLISHED, updateBook.getStatus());
        assertEquals(publishedDate, updateBook.getPublishedAt());
        assertEquals(1, bookDTO.getAuthors().size());
        assertEquals(getAuthorDTOs().get(0).getId(), bookDTO.getAuthors().get(0).getId());
        verify(bookRepository).findById(bookId);
        verify(authorRepository).findAllByIdIn(anyList());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_throwsExceptionWhenAuthorDTOIsEmpty() {
        // Given
        Long bookId = 1L;
        Book book = getBooks().get(0);
        BookDTO bookDTO = getBookDTOs().get(0);
        bookDTO.setAuthors(null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        //when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> bookService.updateBook(bookDTO, bookId));

        //then
        assertNotNull(exception);
        assertEquals("Author list cannot be empty", exception.getMessage());
    }

    @Test
    void updateBook_throwsRecordNotFoundWhenAuthorIsEmpty() {
        // Given
        Long bookId = 1L;
        Book book = getBooks().get(0);
        BookDTO bookDTO = getBookDTOs().get(0);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findAllByIdIn(anyList())).thenReturn(List.of());

        //when
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class, () -> bookService.updateBook(bookDTO, bookId));

        //then
        assertNotNull(exception);
        assertEquals("Some authors were not found", exception.getMessage());
    }

    @Test
    void getAllBooks_whenAuthorIsEmpty() {
        // Given
        List<Long> authorIds = List.of();
        List<Book> books = getBooks();
        List<BookDTO> expectedBookDTOs = getBookDTOs();
        AtomicInteger index = new AtomicInteger(0);


        when(bookRepository.findAllByFlag(Flag.ENABLED)).thenReturn(books);
        when(bookMapper.bookToBookDTO(any(Book.class)))
                .thenAnswer(invocation -> expectedBookDTOs.get(index.getAndIncrement()));

        //when
        List<BookDTO> result = bookService.getAllBooks(authorIds);

        //then
        assertNotNull(result);
        assertEquals(expectedBookDTOs.size(), result.size());
        assertEquals(expectedBookDTOs.get(0).getId(), result.get(0).getId());
        assertEquals(expectedBookDTOs.get(1).getId(), result.get(1).getId());
    }

    @Test
    void getAllBooks_whenAuthors() {
        // Given
        List<Long> authorIds = List.of(1L, 2L);
        List<Book> books = getBooks();
        List<BookDTO> expectedBookDTOs = getBookDTOs();
        AtomicInteger index = new AtomicInteger(0);

        when(authorRepository.findAllByIdIn(authorIds)).thenReturn(getAuthors());
        when(bookRepository.findAllByAuthorsAndFlag(getAuthors(), Flag.ENABLED)).thenReturn(books);
        when(bookMapper.bookToBookDTO(any(Book.class)))
                .thenAnswer(invocation -> expectedBookDTOs.get(index.getAndIncrement()));
        //when
        List<BookDTO> result = bookService.getAllBooks(authorIds);

        //then
        assertNotNull(result);
        assertEquals(expectedBookDTOs.size(), result.size());
        assertEquals(expectedBookDTOs.get(0).getId(), result.get(0).getId());
        assertEquals(expectedBookDTOs.get(1).getId(), result.get(1).getId());
    }

    @Test
    void getBookById() {
        // Given
        Long bookId = 1L;
        Book book = getBooks().get(0);
        BookDTO bookDTO = getBookDTOs().get(0);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);

        //when
        BookDTO result = bookService.getBookById(bookId);

        //then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals(Flag.ENABLED, result.getFlag());
        assertEquals(Status.REVIEW, result.getStatus());
        assertEquals(1, result.getAuthors().size());
        assertEquals(getAuthorDTOs().get(0), result.getAuthors().get(0));
        verify(bookRepository).findById(bookId);
    }

    @Test
    void getBookById_recordNotFound() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //when
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class, () -> bookService.getBookById(bookId));

        assertNotNull(exception);
        assertEquals("Book Not Found 1", exception.getMessage());
        verify(bookRepository).findById(bookId);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void getDeleteById() {
        // Given
        Long bookId = 1L;
        Book book = getBooks().get(0);
        BookDTO bookDTO = getBookDTOs().get(0);
        bookDTO.setFlag(Flag.DISABLED);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDTO(book)).thenReturn(bookDTO);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        //when
        BookDTO result = bookService.deleteBook(bookId);

        //then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals(Flag.DISABLED, result.getFlag());
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void getDeleteById_recordNotFound() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //when
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class, () -> bookService.getBookById(bookId));

        assertNotNull(exception);
        assertEquals("Book Not Found 1", exception.getMessage());
        verify(bookRepository).findById(bookId);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void publishBook() {
    }
}