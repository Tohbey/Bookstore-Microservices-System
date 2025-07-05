package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookstorestarter.enums.Flag;
import com.bookstore.bookstorestarter.enums.StoreType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class BookStoreRepositoryTest {

    @Autowired
    private BookStoreRepository bookStoreRepository;

    BookStore bookStore1;
    BookStore bookStore2;
    BookStore bookStore3;

    @BeforeEach
    void setUp() {
        bookStoreRepository.deleteAll();

        bookStore1 = new BookStore();
        bookStore1.setName("Downtown Bookstore");
        bookStore1.setDescription("A cozy downtown bookstore.");
        bookStore1.setContactEmail("contact@downtownbooks.com");
        bookStore1.setContactPhone("123-456-7890");
        bookStore1.setType(StoreType.PHYSICAL);
        bookStore1.setFlag(Flag.ENABLED);

        BookStore.Address address1 = new BookStore.Address();
        address1.setStreet("123 Main St");
        address1.setCity("Metropolis");
        address1.setState("Metro State");
        address1.setCountry("Country A");
        address1.setPostalCode("11111");
        bookStore1.setAddress(address1);


        bookStore2 = new BookStore();
        bookStore2.setName("Online Reads");
        bookStore2.setDescription("An all-digital bookstore.");
        bookStore2.setContactEmail("support@onlinereads.com");
        bookStore2.setContactPhone("987-654-3210");
        bookStore2.setType(StoreType.DIGITAL);
        bookStore2.setFlag(Flag.ENABLED);

        BookStore.Address address2 = new BookStore.Address();
        address2.setStreet("456 Virtual Blvd");
        address2.setCity("Cyber City");
        address2.setState("Tech State");
        address2.setCountry("Country B");
        address2.setPostalCode("22222");
        bookStore2.setAddress(address2);

        bookStore3 = new BookStore();
        bookStore3.setName("Campus Books");
        bookStore3.setDescription("Bookstore near the university campus.");
        bookStore3.setContactEmail("info@campusbooks.edu");
        bookStore3.setContactPhone("555-555-5555");
        bookStore3.setType(StoreType.PHYSICAL);
        bookStore3.setFlag(Flag.ENABLED);

        BookStore.Address address3 = new BookStore.Address();
        address3.setStreet("789 College Ave");
        address3.setCity("University Town");
        address3.setState("Edu State");
        address3.setCountry("Country C");
        address3.setPostalCode("33333");
        bookStore3.setAddress(address3);

        bookStore1 = bookStoreRepository.save(bookStore1);
        bookStore2 = bookStoreRepository.save(bookStore2);
        bookStore3 = bookStoreRepository.save(bookStore3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByName() {
        String storeName = "Downtown Bookstore";
        Optional<BookStore> bookStore = bookStoreRepository.findByName(storeName);

        assertNotNull(bookStore);
        assertTrue(bookStore.isPresent());
        assertEquals(storeName, bookStore.get().getName());
    }

    @Test
    void findAllByFlag() {
        List<BookStore> bookStores = bookStoreRepository.findAllByFlag(Flag.ENABLED);

        assertNotNull(bookStores);
        assertEquals(3, bookStores.size());
        assertTrue(bookStores.contains(bookStore1));
        assertTrue(bookStores.contains(bookStore2));
        assertTrue(bookStores.contains(bookStore3));
    }
}