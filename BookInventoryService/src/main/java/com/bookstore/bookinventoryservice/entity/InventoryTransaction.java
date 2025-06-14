package com.bookstore.bookinventoryservice.entity;

import com.bookstore.bookinventoryservice.entity.core.FlagableAuditableEntity;
import com.bookstore.bookinventoryservice.enums.InventoryAction;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "InventoryTransactions")
public class InventoryTransaction extends FlagableAuditableEntity {

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    private String transactionRef;
    private Long bookId;
    private Long userId;

    private String reason;

    private int quantity;
    @Enumerated(EnumType.STRING)
    private InventoryAction action;
}
