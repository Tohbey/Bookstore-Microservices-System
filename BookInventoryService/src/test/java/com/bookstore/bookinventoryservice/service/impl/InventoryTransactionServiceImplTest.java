package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.dtos.BorrowAndReturnEvent;
import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import com.bookstore.bookinventoryservice.enums.InventoryAction;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.InventoryTransactionMapper;
import com.bookstore.bookinventoryservice.repository.BookStoreRepository;
import com.bookstore.bookinventoryservice.repository.InventoryRepository;
import com.bookstore.bookinventoryservice.repository.InventoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.bookstore.bookinventoryservice.mock.MockData.getInventories;
import static com.bookstore.bookinventoryservice.mock.MockData.getInventoryTransactionDTOs;
import static com.bookstore.bookinventoryservice.mock.MockData.getInventoryTransactions;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InventoryTransactionServiceImplTest {

    @Mock
    private InventoryTransactionMapper inventoryTransactionMapper;

    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookStoreRepository bookStoreRepository;

    @InjectMocks
    private InventoryTransactionServiceImpl inventoryTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createTransaction_success() {
        InventoryTransactionDTO inventoryTransactionDTO = getInventoryTransactionDTOs().get(0);
        Inventory inventory = getInventories().get(0);

        InventoryTransaction inventoryTransaction = getInventoryTransactions().get(0);

        // Arrange
        when(inventoryRepository.findById(inventoryTransactionDTO.getInventory().getId())).thenReturn(Optional.of(inventory));
        when(inventoryTransactionMapper.inventoryTransactionDTOToInventoryTransaction(inventoryTransactionDTO)).thenReturn(inventoryTransaction);
        when(inventoryTransactionRepository.save(any(InventoryTransaction.class))).thenReturn(inventoryTransaction);
        when(inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(inventoryTransaction)).thenReturn(inventoryTransactionDTO);

        // Act
        InventoryTransactionDTO result = inventoryTransactionService.create(inventoryTransactionDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTransactionRef());
        assertEquals(inventoryTransactionDTO.getUserId(), result.getUserId());

        // Verify interactions
        verify(inventoryRepository).findById(inventoryTransactionDTO.getInventory().getId());
        verify(inventoryTransactionMapper).inventoryTransactionDTOToInventoryTransaction(inventoryTransactionDTO);
        verify(inventoryTransactionRepository).save(inventoryTransaction);
        verify(inventoryTransactionMapper).inventoryTransactionToInventoryTransactionDTO(inventoryTransaction);
    }

    @Test
    void createTransaction_inventoryNotFound() {
        InventoryTransactionDTO inventoryTransactionDTO = getInventoryTransactionDTOs().get(0);

        // Arrange
        when(inventoryRepository.findById(inventoryTransactionDTO.getInventory().getId())).thenReturn(Optional.empty());

        // Act & Assert
        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> inventoryTransactionService.create(inventoryTransactionDTO)
        );

        assertEquals("Inventory Not Found " + inventoryTransactionDTO.getInventory().getId(), exception.getMessage());
        verify(inventoryRepository).findById(inventoryTransactionDTO.getInventory().getId());
        verifyNoInteractions(inventoryTransactionRepository);
        verifyNoInteractions(inventoryTransactionMapper);
    }

    @Test
    void viewTransactionHistory_success() {
        Inventory inventory = getInventories().get(0);
        Long inventoryId = inventory.getId();
        List<InventoryTransaction> transactions = getInventoryTransactions();
        List<InventoryTransactionDTO> transactionDTOs = getInventoryTransactionDTOs();

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(inventoryTransactionRepository.findAllByInventory(inventory)).thenReturn(transactions);
        when(inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(transactions.get(0)))
                .thenReturn(transactionDTOs.get(0));
        when(inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(transactions.get(1)))
                .thenReturn(transactionDTOs.get(1));

        List<InventoryTransactionDTO> result = inventoryTransactionService.viewTransactionHistory(inventoryId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(transactionDTOs.get(0).getTransactionRef(), result.get(0).getTransactionRef());
        assertEquals(transactionDTOs.get(1).getTransactionRef(), result.get(1).getTransactionRef());

        verify(inventoryRepository).findById(inventoryId);
        verify(inventoryTransactionRepository).findAllByInventory(inventory);
        verify(inventoryTransactionMapper, times(2)).inventoryTransactionToInventoryTransactionDTO(any());
    }

    @Test
    void viewTransactionHistory_inventoryNotFound() {
        Long invalidId = 999L;

        when(inventoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> inventoryTransactionService.viewTransactionHistory(invalidId)
        );

        assertEquals("Inventory Not Found " + invalidId, exception.getMessage());
        verify(inventoryRepository).findById(invalidId);
        verifyNoInteractions(inventoryTransactionRepository);
        verifyNoInteractions(inventoryTransactionMapper);
    }

    @Test
    void getTransaction_success() {
        InventoryTransaction inventoryTransaction = getInventoryTransactions().get(0);
        InventoryTransactionDTO inventoryTransactionDTO = getInventoryTransactionDTOs().get(0);
        Long id = inventoryTransaction.getId();

        when(inventoryTransactionRepository.findById(id))
                .thenReturn(Optional.of(inventoryTransaction));

        when(inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(inventoryTransaction))
                .thenReturn(inventoryTransactionDTO);

        InventoryTransactionDTO result = inventoryTransactionService.getTransaction(id);

        assertNotNull(result);
        assertEquals(inventoryTransactionDTO.getTransactionRef(), result.getTransactionRef());
        assertEquals(inventoryTransactionDTO.getBookId(), result.getBookId());
        assertEquals(inventoryTransactionDTO.getUserId(), result.getUserId());

        verify(inventoryTransactionRepository).findById(id);
        verify(inventoryTransactionMapper).inventoryTransactionToInventoryTransactionDTO(inventoryTransaction);
    }

    @Test
    void getTransaction_notFound_throwsException() {
        Long id = 999L;

        when(inventoryTransactionRepository.findById(id)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(
                RecordNotFoundException.class,
                () -> inventoryTransactionService.getTransaction(id)
        );

        assertEquals("Transaction Not Found " + id, exception.getMessage());
    }

    @Test
    void handleBorrowEvent_shouldSucceed_whenEnoughCopiesAvailable() {
        // Given
        BorrowAndReturnEvent event = getBorrowEvent(InventoryAction.BORROWED, 2);

        Inventory inventory = getInventories().get(0);

        when(bookStoreRepository.findById(event.getStoreId()))
                .thenReturn(Optional.of(new BookStore()));

        when(inventoryRepository.findById(event.getInventoryId()))
                .thenReturn(Optional.of(inventory));

        int expectedCount = inventory.getAvailableCopies() - event.getQuantity();

        // When
        inventoryTransactionService.handleBorrowAndReturnEvent(event);

        // Then
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryTransactionRepository).save(any(InventoryTransaction.class));
        assertEquals(expectedCount, inventory.getAvailableCopies());
    }

    @Test
    void handleBorrowEvent_shouldFail_whenNotEnoughCopiesAvailable() {
        // Given
        BorrowAndReturnEvent event = getBorrowEvent(InventoryAction.BORROWED, 40);

        Inventory inventory = getInventories().get(0);

        when(bookStoreRepository.findById(event.getStoreId()))
                .thenReturn(Optional.of(new BookStore()));
        when(inventoryRepository.findById(event.getInventoryId()))
                .thenReturn(Optional.of(inventory));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            inventoryTransactionService.handleBorrowAndReturnEvent(event);
        });

        assertEquals("Not enough available copies to borrow.", ex.getMessage());
        verify(inventoryRepository, never()).save(any());
        verify(inventoryTransactionRepository, never()).save(any());
    }

    @Test
    void handleReturnEvent_shouldSucceed_whenReturnIsValid() {
        // Given
        BorrowAndReturnEvent event = getBorrowEvent(InventoryAction.RETURNED, 2);
        Inventory inventory = getInventories().get(0);
        when(bookStoreRepository.findById(event.getStoreId()))
                .thenReturn(Optional.of(new BookStore()));
        when(inventoryRepository.findById(event.getInventoryId()))
                .thenReturn(Optional.of(inventory));

        int expectedCount = inventory.getAvailableCopies() + event.getQuantity();

        // When
        inventoryTransactionService.handleBorrowAndReturnEvent(event);

        // Then
        verify(inventoryRepository).save(any());
        verify(inventoryTransactionRepository).save(any());
        assertEquals(expectedCount, inventory.getAvailableCopies());
    }

    @Test
    void handleReturnEvent_shouldFail_whenReturningMoreThanBorrowed() {
        // Given
        BorrowAndReturnEvent event = getBorrowEvent(InventoryAction.RETURNED, 40);

        Inventory inventory = getInventories().get(0);

        when(bookStoreRepository.findById(event.getStoreId()))
                .thenReturn(Optional.of(new BookStore()));
        when(inventoryRepository.findById(event.getInventoryId()))
                .thenReturn(Optional.of(inventory));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            inventoryTransactionService.handleBorrowAndReturnEvent(event);
        });

        assertEquals("Cannot return more books than were borrowed.", ex.getMessage());
        verify(inventoryRepository, never()).save(any());
        verify(inventoryTransactionRepository, never()).save(any());
    }

    private BorrowAndReturnEvent getBorrowEvent(InventoryAction action, int quantity) {
        BorrowAndReturnEvent event = new BorrowAndReturnEvent();
        event.setStoreId(1L);
        event.setInventoryId(10L);
        event.setBookId(100L);
        event.setUserId(200L);
        event.setAction(action);
        event.setQuantity(quantity);
        event.setReason("Test reason");
        return event;
    }
}