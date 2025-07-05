package com.bookstore.bookinventoryservice.mapper.dtos;

import com.bookstore.bookstorestarter.enums.InventoryAction;
import lombok.Data;

@Data
public class InventoryTransactionDTO extends BaseDTO {
    private InventoryDTO inventory;

    private String transactionRef;

    private Long bookId;

    private Long userId;

    private String reason;

    private int quantity;

    private InventoryAction action;
}
