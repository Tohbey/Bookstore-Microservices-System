package com.bookstore.bookinventoryservice.dtos;

import com.bookstore.bookinventoryservice.enums.InventoryAction;
import lombok.Data;

@Data
public class BorrowAndReturnEvent {

    private Long bookId;

    private Long storeId;

    private Long inventoryId;

    private String reason;

    private Long userId;

    private Integer quantity;

    private InventoryAction action;
}
