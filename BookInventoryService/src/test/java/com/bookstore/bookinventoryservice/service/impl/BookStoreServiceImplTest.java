package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.BookStoreMapper;
import com.bookstore.bookinventoryservice.repository.BookStoreRepository;
import com.bookstore.bookstorestarter.enums.Flag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.bookstore.bookinventoryservice.mock.MockData.getBookStoreDTOs;
import static com.bookstore.bookinventoryservice.mock.MockData.getBookStores;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookStoreServiceImplTest {

    @Mock
    private BookStoreRepository bookStoreRepository;

    @Mock
    private BookStoreMapper bookStoreMapper;

    @InjectMocks
    private BookStoreServiceImpl bookStoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBookStore_Success() {
        BookStoreDTO bookStoreDTO = getBookStoreDTOs().get(0);
        BookStore bookStore = getBookStores().get(0);

        // mock findByName to throw exception (meaning no existing store found)
        when(bookStoreRepository.findByName("Central Book Haven"))
                .thenReturn(Optional.empty());

        when(bookStoreMapper.bookStoreDTOToBookStore(bookStoreDTO)).thenReturn(bookStore);
        when(bookStoreRepository.save(any(BookStore.class))).thenReturn(bookStore);
        when(bookStoreMapper.bookStoreToBookStoreDTO(bookStore)).thenReturn(bookStoreDTO);

        BookStoreDTO result = bookStoreService.createBookStore(bookStoreDTO);

        assertNotNull(result);
        assertEquals(bookStoreDTO.getName(), result.getName());
        verify(bookStoreRepository).findByName("Central Book Haven");
        verify(bookStoreRepository).save(any(BookStore.class));
    }

    @Test
    void testCreateBookStore_AlreadyExists_ShouldThrowException() {
        BookStore bookStore = getBookStores().get(0);
        BookStoreDTO bookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreRepository.findByName("Central Book Haven")).thenReturn(Optional.of(bookStore));

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> bookStoreService.createBookStore(bookStoreDTO)
        );

        assertEquals("Book Store already exists with name Central Book Haven", exception.getMessage());
        verify(bookStoreRepository).findByName("Central Book Haven");
        verify(bookStoreRepository, never()).save(any());
    }



    @Test
    void testGetBookStoreById_Success() {
        BookStore bookStore = getBookStores().get(0);
        BookStoreDTO bookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreRepository.findById(1L)).thenReturn(Optional.of(bookStore));
        when(bookStoreMapper.bookStoreToBookStoreDTO(bookStore)).thenReturn(bookStoreDTO);

        BookStoreDTO result = bookStoreService.getBookStoreById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Central Book Haven", result.getName());

        verify(bookStoreRepository).findById(1L);
        verify(bookStoreMapper).bookStoreToBookStoreDTO(bookStore);
    }

    @Test
    void testGetBookStoreById_NotFound_ShouldThrowException() {
        Long id = 999L;

        when(bookStoreRepository.findById(id)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> bookStoreService.getBookStoreById(id)
        );

        assertEquals("Book Store Not Found " + id, exception.getMessage());
        verify(bookStoreRepository).findById(id);
        verify(bookStoreMapper, never()).bookStoreToBookStoreDTO(any());
    }

    @Test
    void testGetAllBookStores_ReturnsEnabledStores() {
        List<BookStore> bookStores = getBookStores();
        List<BookStoreDTO> bookStoreDTOs = getBookStoreDTOs();

        when(bookStoreRepository.findAllByFlag(Flag.ENABLED)).thenReturn(bookStores);
        when(bookStoreMapper.bookStoreToBookStoreDTO(bookStores.get(0))).thenReturn(bookStoreDTOs.get(0));
        when(bookStoreMapper.bookStoreToBookStoreDTO(bookStores.get(1))).thenReturn(bookStoreDTOs.get(1));

        List<BookStoreDTO> result = bookStoreService.getAllBookStores();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Flag.ENABLED, result.get(0).getFlag());
        assertEquals(Flag.ENABLED, result.get(1).getFlag());

        verify(bookStoreRepository).findAllByFlag(Flag.ENABLED);
        verify(bookStoreMapper).bookStoreToBookStoreDTO(bookStores.get(0));
        verify(bookStoreMapper).bookStoreToBookStoreDTO(bookStores.get(1));
    }


    @Test
    void testGetAllBookStores_EmptyList() {
        when(bookStoreRepository.findAllByFlag(Flag.ENABLED)).thenReturn(Collections.emptyList());

        List<BookStoreDTO> result = bookStoreService.getAllBookStores();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookStoreRepository).findAllByFlag(Flag.ENABLED);
        verifyNoInteractions(bookStoreMapper);
    }

    @Test
    void updateBookStore() {
        BookStore bookStore = getBookStores().get(0);
        BookStoreDTO bookStoreDTO = getBookStoreDTOs().get(0);
        bookStoreDTO.setName("New Store");
        bookStoreDTO.getAddress().setCity("New City");

        when(bookStoreRepository.findById(1L)).thenReturn(Optional.of(bookStore));
        when(bookStoreRepository.save(any(BookStore.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookStoreMapper.bookStoreToBookStoreDTO(any(BookStore.class))).thenReturn(bookStoreDTO);

        BookStoreDTO updatedDTO = bookStoreService.updateBookStore(bookStoreDTO, 1L);

        assertNotNull(updatedDTO);
        assertEquals("New Store", updatedDTO.getName());
        assertEquals("New City", updatedDTO.getAddress().getCity());

        verify(bookStoreRepository).findById(1L);
        verify(bookStoreRepository).save(bookStore);
        verify(bookStoreMapper).bookStoreToBookStoreDTO(bookStore);
    }

    @Test
    void testUpdateBookStore_BookStoreNotFound() {
        BookStoreDTO bookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreRepository.findById(1L)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> bookStoreService.updateBookStore(bookStoreDTO, 1L));

        assertEquals("Book Store Not Found 1", exception.getMessage());
        verify(bookStoreRepository).findById(1L);
        verify(bookStoreRepository, never()).save(any());
        verify(bookStoreMapper, never()).bookStoreToBookStoreDTO(any());
    }


    @Test
    void deleteBookStore() {
        Long bookStoreId = 1L;
        BookStore bookStore = getBookStores().get(0);
        BookStoreDTO bookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreRepository.findById(bookStoreId)).thenReturn(Optional.of(bookStore));
        when(bookStoreRepository.save(any(BookStore.class))).thenAnswer(i -> i.getArgument(0));
        when(bookStoreMapper.bookStoreToBookStoreDTO(any(BookStore.class))).thenReturn(bookStoreDTO);

        BookStoreDTO result = bookStoreService.deleteBookStore(bookStoreId);

        assertNotNull(result);
        assertEquals(bookStoreDTO.getId(), result.getId());

        assertEquals(Flag.DISABLED, bookStore.getFlag()); // originally ENABLED
        verify(bookStoreRepository).findById(bookStoreId);
        verify(bookStoreRepository).save(bookStore);
        verify(bookStoreMapper).bookStoreToBookStoreDTO(bookStore);
    }

    @Test
    void testDeleteBookStore_NotFound() {
        Long bookStoreId = 99L;

        when(bookStoreRepository.findById(bookStoreId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> bookStoreService.deleteBookStore(bookStoreId));

        assertEquals("Book Store Not Found 99", exception.getMessage());
        verify(bookStoreRepository).findById(bookStoreId);
        verify(bookStoreRepository, never()).save(any());
        verify(bookStoreMapper, never()).bookStoreToBookStoreDTO(any());
    }

}