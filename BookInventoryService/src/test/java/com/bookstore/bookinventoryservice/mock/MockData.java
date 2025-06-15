package com.bookstore.bookinventoryservice.mock;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.enums.InventoryStatus;
import com.bookstore.bookinventoryservice.enums.StoreType;
import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Inventory> getInventories() {
        List<Inventory> inventories = new ArrayList<>();

        Inventory inventory1 = new Inventory();
        inventory1.setId(1L);
        inventory1.setBookId(1001L);
        inventory1.setBookStore(getBookStores().get(0));
        inventory1.setTotalCopies(50);
        inventory1.setAvailableCopies(30);
        inventory1.setStatus(InventoryStatus.ACTIVE);
        inventory1.setLastRestockedAt(LocalDateTime.now().minusDays(3));

        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setBookId(1002L);
        inventory2.setBookStore(getBookStores().get(1));
        inventory2.setTotalCopies(100);
        inventory2.setAvailableCopies(75);
        inventory2.setStatus(InventoryStatus.OUT_OF_STOCK);
        inventory2.setLastRestockedAt(LocalDateTime.now().minusDays(7));

        inventories.add(inventory1);
        inventories.add(inventory2);

        return inventories;
    }


    public static List<InventoryDTO> getInventoryDTOs() {
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();

        InventoryDTO inventory1 = new InventoryDTO();
        inventory1.setId(1L);
        inventory1.setBookId(100L);
        inventory1.setBookStore(getBookStoreDTOs().get(0));
        inventory1.setTotalCopies(50);
        inventory1.setAvailableCopies(30);
        inventory1.setStatus(InventoryStatus.ACTIVE);
        inventory1.setLastRestockedAt(LocalDateTime.now().minusDays(3));

        InventoryDTO inventory2 = new InventoryDTO();
        inventory2.setId(2L);
        inventory2.setBookId(100L);
        inventory2.setBookStore(getBookStoreDTOs().get(1));
        inventory2.setTotalCopies(100);
        inventory2.setAvailableCopies(75);
        inventory2.setStatus(InventoryStatus.OUT_OF_STOCK);
        inventory2.setLastRestockedAt(LocalDateTime.now().minusDays(7));

        inventoryDTOS.add(inventory1);
        inventoryDTOS.add(inventory2);

        return inventoryDTOS;
    }

    public static List<BookStore> getBookStores() {
        List<BookStore> bookStores = new ArrayList<>();

        BookStore store1 = new BookStore();
        store1.setId(1L);
        store1.setName("Central Book Haven");
        store1.setDescription("A central bookstore for all genres.");
        store1.setType(StoreType.PHYSICAL);
        store1.setFlag(Flag.ENABLED);
        store1.setContactEmail("central@bookhaven.com");
        store1.setContactPhone("+1234567890");

        BookStore.Address address1 = new BookStore.Address();
        address1.setStreet("123 Main St");
        address1.setCity("Metropolis");
        address1.setState("Metro State");
        address1.setCountry("Fictionland");
        address1.setPostalCode("12345");
        store1.setAddress(address1);

        BookStore store2 = new BookStore();
        store2.setId(2L);
        store2.setName("Campus Reads");
        store2.setDescription("Serving university students with academic and leisure books.");
        store2.setType(StoreType.PHYSICAL);
        store2.setFlag(Flag.ENABLED);
        store2.setContactEmail("info@campusreads.com");
        store2.setContactPhone("+9876543210");

        BookStore.Address address2 = new BookStore.Address();
        address2.setStreet("45 College Ave");
        address2.setCity("Knowledge City");
        address2.setState("Edu State");
        address2.setCountry("Bookland");
        address2.setPostalCode("67890");
        store2.setAddress(address2);

        bookStores.add(store1);
        bookStores.add(store2);

        return bookStores;
    }

    public static List<BookStoreDTO> getBookStoreDTOs() {
        List<BookStoreDTO> bookStoreDTOS = new ArrayList<>();

        BookStoreDTO bookStoreDTO1 = new BookStoreDTO();
        bookStoreDTO1.setId(1L);
        bookStoreDTO1.setName("Central Book Haven");
        bookStoreDTO1.setDescription("A central bookstore for all genres.");
        bookStoreDTO1.setType(StoreType.PHYSICAL);
        bookStoreDTO1.setFlag(Flag.ENABLED);
        bookStoreDTO1.setContactEmail("central@bookhaven.com");
        bookStoreDTO1.setContactPhone("+1234567890");

        BookStoreDTO.Address address1 = new BookStoreDTO.Address();
        address1.setStreet("123 Main St");
        address1.setCity("Metropolis");
        address1.setState("Metro State");
        address1.setCountry("Fictionland");
        address1.setPostalCode("12345");
        bookStoreDTO1.setAddress(address1);

        BookStoreDTO bookStoreDTO2 = new BookStoreDTO();
        bookStoreDTO2.setId(2L);
        bookStoreDTO2.setName("Campus Reads");
        bookStoreDTO2.setFlag(Flag.ENABLED);
        bookStoreDTO2.setDescription("Serving university students with academic and leisure books.");
        bookStoreDTO2.setType(StoreType.PHYSICAL);
        bookStoreDTO2.setContactEmail("info@campusreads.com");
        bookStoreDTO2.setContactPhone("+9876543210");

        BookStoreDTO.Address address2 = new BookStoreDTO.Address();
        address2.setStreet("45 College Ave");
        address2.setCity("Knowledge City");
        address2.setState("Edu State");
        address2.setCountry("Bookland");
        address2.setPostalCode("67890");
        bookStoreDTO2.setAddress(address2);

        bookStoreDTOS.add(bookStoreDTO1);
        bookStoreDTOS.add(bookStoreDTO2);

        return bookStoreDTOS;
    }

}
