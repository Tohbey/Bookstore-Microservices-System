package com.bookstore.bookinventoryservice.service;

import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;

import java.util.List;

public interface InventoryTransactionService {

    InventoryTransactionDTO create(InventoryTransactionDTO inventoryTransactionDTO);

    List<InventoryTransactionDTO> viewTransactionHistory(Long inventoryId);

    InventoryTransactionDTO getTransaction(Long transactionId);
}
