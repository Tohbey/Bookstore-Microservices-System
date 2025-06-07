package com.bookstore.authorservice.service.impl;

import com.bookstore.authorservice.config.MessageProducer;
import com.bookstore.authorservice.dtos.PublishDto;
import com.bookstore.authorservice.entity.Book;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.enums.Status;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.authorservice.mapper.mappers.BookMapper;
import com.bookstore.authorservice.repository.AuthorRepository;
import com.bookstore.authorservice.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bookstore.authorservice.config.KafkaTopics.BOOK_PUBLISHED;
import static com.bookstore.authorservice.mock.MockData.getAuthorDTOs;
import static com.bookstore.authorservice.mock.MockData.getAuthors;
import static com.bookstore.authorservice.mock.MockData.getBookDTOs;
import static com.bookstore.authorservice.mock.MockData.getBooks;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private BookMapper bookMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.registerModule(new JavaTimeModule());
        ReflectionTestUtils.setField(bookService, "objectMapper", objectMapper);
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
    void testPublishBook_Success() throws JsonProcessingException {
        BookDTO bookDTO = getBookDTOs().get(0);

        PublishDto publishDto = new PublishDto();
        publishDto.setBookDTO(bookDTO);
        publishDto.setPublishedCopies(20);

        Book existingBook = getBooks().get(0);

        Book savedBook = getBooks().get(0);
        savedBook.setStatus(Status.PUBLISHED);
        savedBook.setPublishedAt(LocalDateTime.now());

        BookDTO savedBookDTO = getBookDTOs().get(0);
        savedBookDTO.setStatus(Status.PUBLISHED);
        savedBookDTO.setPublishedAt(LocalDateTime.now());

        when(bookRepository.findById(bookDTO.getId())).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(bookMapper.bookToBookDTO(savedBook)).thenReturn(savedBookDTO);

        BookDTO expectedBookDTO = bookService.publishBook(publishDto);

        // Assert
        assertEquals(80, publishDto.getRemainingCopies());
        assertEquals(expectedBookDTO, savedBookDTO);
        verify(bookRepository).findById(bookDTO.getId());
        verify(bookRepository).save(any(Book.class));
        verify(messageProducer).sendMessage(eq(BOOK_PUBLISHED), anyString());
    }

    @Test
    void testPublishBook_BookNotFound_ThrowsException() {
        PublishDto publishDto = new PublishDto();
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(1L);
        publishDto.setBookDTO(bookDTO);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> {
            bookService.publishBook(publishDto);
        });

        assertEquals("Book Not Found 1", ex.getMessage());
        verify(bookRepository).findById(1L);
        verifyNoMoreInteractions(bookRepository, bookMapper, messageProducer);
    }

    @Test
    void testPublishBook_InsufficientCopies_ThrowsRuntimeException() {
        BookDTO bookDTO = getBookDTOs().get(0);

        PublishDto publishDto = new PublishDto();
        publishDto.setBookDTO(bookDTO);
        publishDto.setPublishedCopies(120);

        Book book = getBooks().get(0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            bookService.publishBook(publishDto);
        });

        assertEquals("Not enough copies available", ex.getMessage());
        verify(bookRepository).findById(1L);
        verifyNoMoreInteractions(bookRepository, bookMapper, messageProducer);
    }
}