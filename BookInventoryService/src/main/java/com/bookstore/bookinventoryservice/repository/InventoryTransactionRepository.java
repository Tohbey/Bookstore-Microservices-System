package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findAllByInventory(Inventory inventory);
}
