package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookstorestarter.enums.Flag;
import com.bookstore.bookstorestarter.enums.InventoryStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BookStoreRepository bookStoreRepository;

    Inventory inventory1;
    Inventory inventory2;
    Inventory inventory3;
    Inventory inventory4;
    BookStore bookStore;
    BookStore bookStore1;

    @BeforeEach
    void setUp() {
        bookStore = new BookStore();
        bookStore.setName("Book Store");
        bookStore.setFlag(Flag.ENABLED);
        bookStore.setDescription("Book Store");
        bookStore = bookStoreRepository.save(bookStore);

        bookStore1 = new BookStore();
        bookStore1.setName("Book Store 1");
        bookStore1.setFlag(Flag.ENABLED);
        bookStore1.setDescription("Book Store 1");
        bookStore1 = bookStoreRepository.save(bookStore1);

        inventory1 = new Inventory();
        inventory2 = new Inventory();
        inventory3 = new Inventory();
        inventory4 = new Inventory();

        inventory1.setBookId(1L);
        inventory1.setBookStore(bookStore);
        inventory1.setTotalCopies(100);
        inventory1.setAvailableCopies(100);
        inventory1.setFlag(Flag.ENABLED);
        inventory1.setStatus(InventoryStatus.ACTIVE);
        inventory1.setTransactions(new ArrayList<>());
        inventory1.setLastRestockedAt(LocalDateTime.now());
        inventory1 = inventoryRepository.save(inventory1);

        inventory2.setBookId(2L);
        inventory2.setBookStore(bookStore1);
        inventory2.setTotalCopies(90);
        inventory2.setAvailableCopies(70);
        inventory2.setFlag(Flag.ENABLED);
        inventory2.setStatus(InventoryStatus.ACTIVE);
        inventory2.setTransactions(new ArrayList<>());
        inventory2.setLastRestockedAt(LocalDateTime.now());
        inventory2 = inventoryRepository.save(inventory2);

        inventory3.setBookId(3L);
        inventory3.setBookStore(bookStore);
        inventory3.setTotalCopies(90);
        inventory3.setFlag(Flag.ENABLED);
        inventory3.setAvailableCopies(90);
        inventory3.setStatus(InventoryStatus.OUT_OF_STOCK);
        inventory3.setTransactions(new ArrayList<>());
        inventory3.setLastRestockedAt(LocalDateTime.now());
        inventory3 = inventoryRepository.save(inventory3);

        inventory4.setBookId(4L);
        inventory4.setBookStore(bookStore);
        inventory4.setTotalCopies(90);
        inventory4.setFlag(Flag.ENABLED);
        inventory4.setAvailableCopies(90);
        inventory4.setStatus(InventoryStatus.DAMAGED);
        inventory4.setTransactions(new ArrayList<>());
        inventory4.setLastRestockedAt(LocalDateTime.now());
        inventory4 = inventoryRepository.save(inventory4);
    }

    @AfterEach
    void tearDown() {
        inventoryRepository.deleteAll();
        bookStoreRepository.deleteAll();
    }

    @Test
    void findAllByBookId() {
        List<Inventory> inventories = inventoryRepository.findAllByBookIdAndFlag(1L, Flag.ENABLED);

        assertNotNull(inventories);
        assertEquals(1, inventories.size());
        assertEquals(inventory1, inventories.get(0));
        assertEquals(bookStore, inventory1.getBookStore());
    }

    @Test
    void findAllByBookStore() {
        List<Inventory> inventories = inventoryRepository.findAllByBookStoreAndFlag(bookStore, Flag.ENABLED);
        assertNotNull(inventories);
        assertEquals(3, inventories.size());
        assertEquals(inventory1, inventories.get(0));
        assertEquals(bookStore, inventories.get(0).getBookStore());
        assertEquals(Flag.ENABLED, inventories.get(0).getFlag());
        assertEquals(bookStore, inventories.get(1).getBookStore());
        assertEquals(Flag.ENABLED, inventories.get(1).getFlag());
        assertEquals(bookStore, inventories.get(2).getBookStore());
        assertEquals(Flag.ENABLED, inventories.get(2).getFlag());
    }

    @Test
    void findAllByFlag() {
        List<Inventory> inventories = inventoryRepository.findAllByFlag(Flag.ENABLED);
        assertNotNull(inventories);
        assertEquals(4, inventories.size());
        assertEquals(inventory1, inventories.get(0));
        assertEquals(inventory2, inventories.get(1));
        assertEquals(inventory3, inventories.get(2));
        assertEquals(inventory4, inventories.get(3));
    }

    @Test
    void findAllByBookStoreAndBookIdAndFlag() {
        List<Inventory> inventories = inventoryRepository.findAllByBookStoreAndBookIdAndFlag(bookStore, 1L, Flag.ENABLED);
        assertNotNull(inventories);
        assertEquals(1, inventories.size());
        assertEquals(inventory1, inventories.get(0));;
    }
}