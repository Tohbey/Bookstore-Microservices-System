package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.InventoryTransactionMapper;
import com.bookstore.bookinventoryservice.repository.BookStoreRepository;
import com.bookstore.bookinventoryservice.repository.InventoryRepository;
import com.bookstore.bookinventoryservice.repository.InventoryTransactionRepository;
import com.bookstore.bookinventoryservice.service.InventoryTransactionService;
import com.bookstore.bookstorestarter.dtos.BorrowAndReturnEvent;
import com.bookstore.bookstorestarter.enums.Flag;
import com.bookstore.bookstorestarter.enums.InventoryAction;
import com.bookstore.bookstorestarter.enums.InventoryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final InventoryTransactionMapper inventoryTransactionMapper;

    private final InventoryRepository inventoryRepository;

    private final BookStoreRepository bookStoreRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    Logger logger = LoggerFactory.getLogger(InventoryTransactionServiceImpl.class);

    public InventoryTransactionServiceImpl(InventoryTransactionRepository inventoryTransactionRepository,
                                           InventoryTransactionMapper inventoryTransactionMapper,
                                           InventoryRepository inventoryRepository, BookStoreRepository bookStoreRepository) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
        this.inventoryRepository = inventoryRepository;
        this.bookStoreRepository = bookStoreRepository;
    }


    @Override
    public InventoryTransactionDTO create(InventoryTransactionDTO inventoryTransactionDTO) {
        logger.info("Creating a new inventory transaction");
        Inventory inventory = inventoryRepository.findById(inventoryTransactionDTO.getInventory().getId())
                .orElseThrow(() -> new RecordNotFoundException("Inventory Not Found "+inventoryTransactionDTO.getInventory().getId()));

        InventoryTransaction transaction = inventoryTransactionMapper.inventoryTransactionDTOToInventoryTransaction(inventoryTransactionDTO);
        transaction.setInventory(inventory);
        transaction.setTransactionRef(generateTransactionRef());

        transaction = inventoryTransactionRepository.save(transaction);

        return inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(transaction);
    }

    @Override
    public List<InventoryTransactionDTO> viewTransactionHistory(Long inventoryId) {
        logger.info("Getting the list of transactions by inventory id {}",inventoryId);

        List<InventoryTransactionDTO> inventoryTransactionDTOS = new ArrayList<>();
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RecordNotFoundException("Inventory Not Found "+inventoryId));

        List<InventoryTransaction> inventoryTransactions = inventoryTransactionRepository.findAllByInventory(inventory);

        for (InventoryTransaction inventoryTransaction : inventoryTransactions) {
            inventoryTransactionDTOS.add(inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(inventoryTransaction));
        }

        return inventoryTransactionDTOS;
    }

    @Override
    public InventoryTransactionDTO getTransaction(Long transactionId) {
        logger.info("Getting the transaction by inventory id {}",transactionId);
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RecordNotFoundException("Transaction Not Found "+transactionId));

        return inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(inventoryTransaction);
    }

    @Override
    public void handleBorrowAndReturnEvent(BorrowAndReturnEvent event) {
        BookStore bookStore = bookStoreRepository.findById(event.getStoreId())
                .orElseThrow(() -> new RecordNotFoundException("Book store Not Found "+event.getStoreId()));

        Inventory inventory = inventoryRepository.findById(event.getInventoryId())
                .orElseThrow(() -> new RecordNotFoundException("Inventory Not Found "+event.getStoreId()));

        if (event.getAction() == InventoryAction.BORROWED) {
            if (inventory.getAvailableCopies() < event.getQuantity()) {
                throw new RuntimeException("Not enough available copies to borrow.");
            }
            inventory.setAvailableCopies(inventory.getAvailableCopies() - event.getQuantity());

        } else if (event.getAction() == InventoryAction.RETURNED) {
            int maxReturnable = inventory.getTotalCopies() - inventory.getAvailableCopies();
            if (event.getQuantity() > maxReturnable) {
                throw new RuntimeException("Cannot return more books than were borrowed.");
            }
            inventory.setAvailableCopies(inventory.getAvailableCopies() + event.getQuantity());
        }

        inventory.setStatus(inventory.getAvailableCopies() == 0
                ? InventoryStatus.OUT_OF_STOCK
                : InventoryStatus.ACTIVE);

        inventoryRepository.save(inventory);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionRef(generateTransactionRef());
        transaction.setInventory(inventory);
        transaction.setFlag(Flag.ENABLED);
        transaction.setBookId(event.getBookId());
        transaction.setUserId(event.getUserId());
        transaction.setAction(event.getAction());
        transaction.setQuantity(event.getQuantity());
        transaction.setReason(event.getReason());

        inventoryTransactionRepository.save(transaction);

        logger.info("Inventory updated and transaction recorded for bookId: {}", event.getBookId());
    }


    private static String generateTransactionRef() {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "TXN-" + timestamp + "-" + uniquePart;
    }
}
