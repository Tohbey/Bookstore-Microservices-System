package com.bookstore.bookinventoryservice.entity;

import com.bookstore.bookinventoryservice.entity.core.FlagableAuditableEntity;
import com.bookstore.bookstorestarter.enums.Flag;
import com.bookstore.bookstorestarter.enums.InventoryStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Inventories")
public class Inventory extends FlagableAuditableEntity {

   private Long bookId;

   @ManyToOne
   @JoinColumn(name = "bookstore_id")
   private BookStore bookStore;

   private int totalCopies;

   private int availableCopies;

   @Enumerated(EnumType.STRING)
   private InventoryStatus status;

   private LocalDateTime lastRestockedAt;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryTransaction> transactions = new ArrayList<>();
}