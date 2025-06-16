package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.InventoryTransactionMapper;
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

        assertEquals("Transaction Not Found " + inventoryTransactionDTO.getInventory().getId(), exception.getMessage());
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
}