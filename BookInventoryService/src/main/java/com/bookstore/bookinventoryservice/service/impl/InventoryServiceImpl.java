package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.dtos.PublishEvent;
import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.enums.InventoryStatus;
import com.bookstore.bookinventoryservice.exception.RecordAlreadyExistException;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.InventoryMapper;
import com.bookstore.bookinventoryservice.repository.BookStoreRepository;
import com.bookstore.bookinventoryservice.repository.InventoryRepository;
import com.bookstore.bookinventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final BookStoreRepository bookStoreRepository;

    private final InventoryMapper inventoryMapper;

    Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    public InventoryServiceImpl(InventoryRepository inventoryRepository, BookStoreRepository bookStoreRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.bookStoreRepository = bookStoreRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        logger.info("Creating inventory detail");
        BookStore bookStore = bookStoreRepository.findById(inventoryDTO.getBookStore().getId())
                .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+inventoryDTO.getBookStore().getId()));

        Optional<Inventory> existingInventory = inventoryRepository.findByBookIdAndFlag(inventoryDTO.getBookId(), Flag.ENABLED);

        if(existingInventory.isPresent() && existingInventory.get().getStatus().equals(InventoryStatus.ACTIVE)){
            throw new RecordAlreadyExistException("Inventory already exists for this book");
        }

        Inventory inventory = inventoryMapper.inventoryDTOToInventory(inventoryDTO);

        inventory.setBookStore(bookStore);

        inventory = inventoryRepository.save(inventory);

        return inventoryMapper.InventoryToInventoryDTO(inventory);
    }

    @Override
    public InventoryDTO updateInventory(InventoryDTO inventoryDTO, Long inventoryId) {
        logger.info("Updating inventory detail {}", inventoryId);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RecordNotFoundException("Inventory Not Found "+inventoryId));
        BookStore bookStore = bookStoreRepository.findById(inventoryDTO.getBookStore().getId())
                .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+inventoryDTO.getBookStore().getId()));

        inventory.setBookStore(bookStore);
        inventory.setFlag(Flag.ENABLED);
        inventory.setStatus(inventoryDTO.getStatus());
        inventory.setAvailableCopies(inventoryDTO.getAvailableCopies());
        inventory.setTotalCopies(inventoryDTO.getTotalCopies());
        inventory.setBookId(inventoryDTO.getBookId());

        inventory = inventoryRepository.save(inventory);

        return inventoryMapper.InventoryToInventoryDTO(inventory);
    }

    @Override
    public InventoryDTO deleteInventory(Long inventoryId) {
        logger.info("Deleting inventory detail {}", inventoryId);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RecordNotFoundException("Inventory Not Found "+inventoryId));

        inventory.setFlag(Flag.DISABLED);
        inventory = inventoryRepository.save(inventory);

        return inventoryMapper.InventoryToInventoryDTO(inventory);
    }

    @Override
    public InventoryDTO getInventoryById(Long inventoryId) {
        logger.info("Fetching inventory detail {}", inventoryId);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RecordNotFoundException("Inventory Not Found "+inventoryId));

        return inventoryMapper.InventoryToInventoryDTO(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventory(Flag flag, Long storeId, Long bookId) {
        logger.info("Fetching inventories detail by flag {} or store id {} or book Id {}", flag, storeId, bookId);
        List<Inventory> inventories;
        List<InventoryDTO> inventoryDTOS = new ArrayList<>();

        if(Objects.nonNull(bookId) && Objects.nonNull(storeId)){
            BookStore bookStore = bookStoreRepository.findById(storeId)
                    .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+storeId));

            inventories = inventoryRepository.findAllByBookStoreAndBookIdAndFlag(bookStore, bookId,flag);
        }else if(Objects.nonNull(bookId)){
            inventories = inventoryRepository.findAllByBookIdAndFlag(bookId, flag);

        } else if (Objects.nonNull(storeId) ) {
            BookStore bookStore = bookStoreRepository.findById(storeId)
                    .orElseThrow(() -> new RecordNotFoundException("Book Store Not Found "+storeId));

            inventories = inventoryRepository.findAllByBookStoreAndFlag(bookStore, flag);
        } else{
            inventories = inventoryRepository.findAllByFlag(flag);
        }

        for(Inventory inventory : inventories){
            inventoryDTOS.add(inventoryMapper.InventoryToInventoryDTO(inventory));
        }

        return inventoryDTOS;
    }

    @Override
    public void handleBookPublishedEvent(PublishEvent event) {
        logger.info("Received book.published event: {}", event.getBookDTO().getId());

        List<BookStore> bookStores = bookStoreRepository.findAllByFlag(Flag.ENABLED);

        for(BookStore bookStore : bookStores){
            Inventory inventory = new Inventory();
            inventory.setBookStore(bookStore);
            inventory.setFlag(Flag.ENABLED);
            inventory.setBookId(event.getBookDTO().getId());
            inventory.setStatus(InventoryStatus.OUT_OF_STOCK);

            inventoryRepository.save(inventory);
        }
    }
}
