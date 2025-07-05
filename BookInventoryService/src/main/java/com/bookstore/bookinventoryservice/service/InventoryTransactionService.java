package com.bookstore.bookinventoryservice.service;

import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookstorestarter.dtos.BorrowAndReturnEvent;

import java.util.List;

public interface InventoryTransactionService {

    InventoryTransactionDTO create(InventoryTransactionDTO inventoryTransactionDTO);

    List<InventoryTransactionDTO> viewTransactionHistory(Long inventoryId);

    InventoryTransactionDTO getTransaction(Long transactionId);

    void handleBorrowAndReturnEvent(BorrowAndReturnEvent borrowAndReturnEvent);
}
