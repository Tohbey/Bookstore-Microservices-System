package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.enums.InventoryStatus;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.InventoryMapper;
import com.bookstore.bookinventoryservice.repository.BookStoreRepository;
import com.bookstore.bookinventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.bookstore.bookinventoryservice.mock.MockData.getBookStores;
import static com.bookstore.bookinventoryservice.mock.MockData.getInventories;
import static com.bookstore.bookinventoryservice.mock.MockData.getInventoryDTOs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookStoreRepository bookStoreRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInventory() {
        // Given
        InventoryDTO inventoryDTO = getInventoryDTOs().get(0);
        Inventory inventory = getInventories().get(0);
        BookStore bookStore = getBookStores().get(0);

        when(bookStoreRepository.findById(inventoryDTO.getBookStore().getId())).thenReturn(Optional.of(bookStore));
        when(inventoryRepository.findByBookIdAndFlag(inventoryDTO.getBookId(), Flag.ENABLED)).thenReturn(Optional.empty());
        when(inventoryMapper.inventoryDTOToInventory(inventoryDTO)).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.InventoryToInventoryDTO(inventory)).thenReturn(inventoryDTO);

        // When
        InventoryDTO result = inventoryService.createInventory(inventoryDTO);

        // Then
        assertNotNull(result);
        assertEquals(inventoryDTO, result);

        verify(bookStoreRepository).findById(inventoryDTO.getBookStore().getId());
        verify(inventoryRepository).findByBookIdAndFlag(inventoryDTO.getBookId(), Flag.ENABLED);
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).InventoryToInventoryDTO(inventory);
    }

    @Test
    void testCreateInventory_BookStoreNotFound() {
        // Given
        InventoryDTO inventoryDTO = getInventoryDTOs().get(0);
        Long storeId = inventoryDTO.getBookStore().getId();

        when(bookStoreRepository.findById(storeId)).thenReturn(Optional.empty());

        // When & Then
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> inventoryService.createInventory(inventoryDTO));

        assertEquals("Book Store Not Found " + storeId, ex.getMessage());

        verify(bookStoreRepository).findById(storeId);
        verifyNoMoreInteractions(inventoryRepository, inventoryMapper);
    }


    @Test
    void testCreateInventory_InventoryExistsAndInactive() {
        // Given
        InventoryDTO inventoryDTO = getInventoryDTOs().get(0);
        BookStore bookStore = getBookStores().get(0);
        Inventory existingInventory = getInventories().get(0);
        existingInventory.setStatus(InventoryStatus.ACTIVE);

        when(bookStoreRepository.findById(inventoryDTO.getBookStore().getId())).thenReturn(Optional.of(bookStore));
        when(inventoryRepository.findByBookIdAndFlag(inventoryDTO.getBookId(), Flag.ENABLED))
                .thenReturn(Optional.of(existingInventory));

        // When & Then
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> inventoryService.createInventory(inventoryDTO));

        assertEquals("Inventory already exists for this book", ex.getMessage());

        verify(bookStoreRepository).findById(inventoryDTO.getBookStore().getId());
        verify(inventoryRepository).findByBookIdAndFlag(inventoryDTO.getBookId(), Flag.ENABLED);
        verifyNoMoreInteractions(inventoryRepository, inventoryMapper);
    }

    @Test
    void updateInventory() {
        // Given
        Long inventoryId = 1L;

        Inventory existingInventory = getInventories().get(0);
        BookStore bookStore = getBookStores().get(0);

        InventoryDTO inventoryDTO = getInventoryDTOs().get(0);

        Inventory updatedInventory = getInventories().get(0);
        updatedInventory.setStatus(InventoryStatus.OUT_OF_STOCK);

        InventoryDTO expectedDTO = getInventoryDTOs().get(0);
        expectedDTO.setStatus(InventoryStatus.OUT_OF_STOCK);


        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
        when(bookStoreRepository.findById(bookStore.getId())).thenReturn(Optional.of(bookStore));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);
        when(inventoryMapper.InventoryToInventoryDTO(updatedInventory)).thenReturn(expectedDTO);

        // When
        InventoryDTO result = inventoryService.updateInventory(inventoryDTO, inventoryId);

        // Then
        assertNotNull(result);
        assertEquals(inventoryId, result.getId());
        assertEquals(InventoryStatus.OUT_OF_STOCK, result.getStatus());

        verify(inventoryRepository).findById(inventoryId);
        verify(bookStoreRepository).findById(bookStore.getId());
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMapper).InventoryToInventoryDTO(updatedInventory);
    }

    @Test
    void testUpdateInventory_InventoryNotFound() {
        // Given
        Long inventoryId = 999L;
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setBookStore(new BookStoreDTO());
        inventoryDTO.getBookStore().setId(1L);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        // When & Then
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> inventoryService.updateInventory(inventoryDTO, inventoryId));

        assertEquals("Inventory Not Found 999", ex.getMessage());
        verify(inventoryRepository).findById(inventoryId);
        verifyNoMoreInteractions(bookStoreRepository, inventoryRepository, inventoryMapper);
    }

    @Test
    void testUpdateInventory_BookStoreNotFound() {
        // Given
        Long inventoryId = 1L;
        Long bookStoreId = 999L;

        Inventory existingInventory = getInventories().get(0);

        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setBookStore(new BookStoreDTO());
        inventoryDTO.getBookStore().setId(bookStoreId);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
        when(bookStoreRepository.findById(bookStoreId)).thenReturn(Optional.empty());

        // When & Then
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> inventoryService.updateInventory(inventoryDTO, inventoryId));

        assertEquals("Book Store Not Found 999", ex.getMessage());
        verify(inventoryRepository).findById(inventoryId);
        verify(bookStoreRepository).findById(bookStoreId);
    }


    @Test
    void deleteInventory() {
        // Given
        Inventory inventory = getInventories().get(0);
        inventory.setFlag(Flag.DISABLED); // Set the flag as expected after deletion

        InventoryDTO expectedDTO = getInventoryDTOs().get(0);
        expectedDTO.setFlag(Flag.DISABLED);

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(inventoryMapper.InventoryToInventoryDTO(inventory)).thenReturn(expectedDTO);

        // When
        InventoryDTO result = inventoryService.deleteInventory(inventory.getId());

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(inventoryRepository).findById(inventory.getId());
        verify(inventoryRepository).save(inventory);
        verify(inventoryMapper).InventoryToInventoryDTO(inventory);
    }

    @Test
    void testDeleteInventory_WhenInventoryNotFound_ShouldThrowException() {
        // Given
        Long inventoryId = 999L;

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        // When & Then
        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            inventoryService.deleteInventory(inventoryId);
        });

        assertEquals("Inventory Not Found 999", exception.getMessage());
        verify(inventoryRepository).findById(inventoryId);
    }

    @Test
    void getInventoryById() {
        //Given
        Long inventoryId = 1L;
        Inventory inventory = getInventories().get(0);
        InventoryDTO inventoryDTO = getInventoryDTOs().get(0);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(inventoryMapper.InventoryToInventoryDTO(inventory)).thenReturn(inventoryDTO);

        // when
        InventoryDTO result = inventoryService.getInventoryById(inventoryId);

        //then
        assertNotNull(result);
        assertEquals(inventoryId, result.getId());
        assertEquals(inventoryDTO, result);

        verify(inventoryRepository).findById(inventoryId);
        verify(inventoryMapper).InventoryToInventoryDTO(inventory);
    }

    @Test
    void getInventoryById_whenInventoryDoesNotExist_throwsException() {
        //Given
        Long inventoryId = 1L;

        // when
        RecordNotFoundException result = assertThrows(
                RecordNotFoundException.class, () -> inventoryService.getInventoryById(inventoryId));

        //then
        assertNotNull(result);
        assertEquals("Inventory Not Found 1", result.getMessage());
        verify(inventoryRepository).findById(inventoryId);
        verifyNoInteractions(inventoryMapper);
    }


    @Test
    void testGetAllInventory_ByStoreId() {
        // Given
        BookStore bookStore = getBookStores().get(0);
        List<Inventory> inventories = getInventories();
        List<InventoryDTO> inventoryDTOs = getInventoryDTOs();

        when(bookStoreRepository.findById(1L)).thenReturn(Optional.of(bookStore));
        when(inventoryRepository.findAllByBookStoreAndFlag(bookStore, Flag.ENABLED)).thenReturn(inventories);
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(0))).thenReturn(inventoryDTOs.get(0));
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(1))).thenReturn(inventoryDTOs.get(1));

        // When
        List<InventoryDTO> result = inventoryService.getAllInventory(Flag.ENABLED, 1L, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertNotNull(result.get(0).getBookStore());
        assertNotNull(result.get(1).getBookStore());

        verify(bookStoreRepository).findById(1L);
        verify(inventoryRepository).findAllByBookStoreAndFlag(bookStore, Flag.ENABLED);
    }


    @Test
    void testGetAllInventory_ByBookId() {
        List<Inventory> inventories = getInventories();
        List<InventoryDTO> inventoryDTOs = getInventoryDTOs();

        when(inventoryRepository.findAllByBookIdAndFlag(100L, Flag.ENABLED)).thenReturn(inventories);
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(0))).thenReturn(inventoryDTOs.get(0));
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(1))).thenReturn(inventoryDTOs.get(1));

        List<InventoryDTO> result = inventoryService.getAllInventory(Flag.ENABLED, null, 100L);

        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getBookId());
        verify(inventoryRepository).findAllByBookIdAndFlag(100L, Flag.ENABLED);
    }

    @Test
    void testGetAllInventory_AllByFlag() {
        List<Inventory> inventories = getInventories();
        List<InventoryDTO> inventoryDTOs = getInventoryDTOs();

        when(inventoryRepository.findAllByFlag(Flag.ENABLED)).thenReturn(inventories);
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(0))).thenReturn(inventoryDTOs.get(0));
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(1))).thenReturn(inventoryDTOs.get(1));

        List<InventoryDTO> result = inventoryService.getAllInventory(Flag.ENABLED, null, null);

        assertEquals(2, result.size());
        verify(inventoryRepository).findAllByFlag(Flag.ENABLED);
    }

    @Test
    void testGetAllInventory_AllByFlagAndBookIdAndBookStore() {
        Long storeId = 1L;
        Long bookId = 2L;
        BookStore bookStore = getBookStores().get(0);
        List<Inventory> inventories = getInventories();
        List<InventoryDTO> inventoryDTOs = getInventoryDTOs();

        when(bookStoreRepository.findById(storeId)).thenReturn(Optional.of(bookStore));
        when(inventoryRepository.findAllByBookStoreAndBookIdAndFlag(bookStore, bookId, Flag.ENABLED)).thenReturn(inventories);
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(0))).thenReturn(inventoryDTOs.get(0));
        when(inventoryMapper.InventoryToInventoryDTO(inventories.get(1))).thenReturn(inventoryDTOs.get(1));

        List<InventoryDTO> result = inventoryService.getAllInventory(Flag.ENABLED, storeId, bookId);

        assertEquals(2, result.size());
        verify(inventoryRepository).findAllByBookStoreAndBookIdAndFlag(bookStore, bookId, Flag.ENABLED);
    }

    @Test
    void testGetAllInventory_BookStoreNotFound() {
        when(bookStoreRepository.findById(2L)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> inventoryService.getAllInventory(Flag.ENABLED, 2L, null)
        );

        assertEquals("Book Store Not Found 2", exception.getMessage());
        verify(bookStoreRepository).findById(2L);
    }
}