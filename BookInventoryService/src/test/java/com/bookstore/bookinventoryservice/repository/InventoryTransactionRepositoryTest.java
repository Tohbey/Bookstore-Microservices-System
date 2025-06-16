package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.enums.InventoryAction;
import com.bookstore.bookinventoryservice.enums.InventoryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class InventoryTransactionRepositoryTest {

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setBookId(101L);
        inventory.setAvailableCopies(50);
        inventory.setTotalCopies(100);
        inventory.setStatus(InventoryStatus.ACTIVE);
        inventory.setFlag(Flag.ENABLED);
        inventory = inventoryRepository.save(inventory);

        InventoryTransaction tx1 = new InventoryTransaction();
        tx1.setInventory(inventory);
        tx1.setBookId(101L);
        tx1.setUserId(1L);
        tx1.setQuantity(5);
        tx1.setAction(InventoryAction.STOCKED);
        tx1.setTransactionRef("TXN001");
        tx1.setReason("Initial stock");

        InventoryTransaction tx2 = new InventoryTransaction();
        tx2.setInventory(inventory);
        tx2.setBookId(101L);
        tx2.setUserId(2L);
        tx2.setQuantity(3);
        tx2.setAction(InventoryAction.DAMAGED);
        tx2.setTransactionRef("TXN002");
        tx2.setReason("Damaged books");

        inventoryTransactionRepository.saveAll(List.of(tx1, tx2));
    }

    @Test
    void findAllByInventory() {
        List<InventoryTransaction> transactions = inventoryTransactionRepository.findAllByInventory(inventory);

        assertNotNull(transactions);
        assertEquals(2, transactions.size());

        assertTrue(transactions.stream().anyMatch(tx -> tx.getTransactionRef().equals("TXN001")));
        assertTrue(transactions.stream().anyMatch(tx -> tx.getTransactionRef().equals("TXN002")));
    }
}