package com.bookstore.bookinventoryservice.service.impl;

import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookinventoryservice.mapper.mappers.InventoryTransactionMapper;
import com.bookstore.bookinventoryservice.repository.InventoryRepository;
import com.bookstore.bookinventoryservice.repository.InventoryTransactionRepository;
import com.bookstore.bookinventoryservice.service.InventoryTransactionService;
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

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    public InventoryTransactionServiceImpl(InventoryTransactionRepository inventoryTransactionRepository, InventoryTransactionMapper inventoryTransactionMapper, InventoryRepository inventoryRepository) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
        this.inventoryRepository = inventoryRepository;
    }


    @Override
    public InventoryTransactionDTO create(InventoryTransactionDTO inventoryTransactionDTO) {
        Inventory inventory = inventoryRepository.findById(inventoryTransactionDTO.getInventory().getId())
                .orElseThrow(() -> new RecordNotFoundException("Transaction Not Found "+inventoryTransactionDTO.getInventory().getId()));

        InventoryTransaction transaction = inventoryTransactionMapper.inventoryTransactionDTOToInventoryTransaction(inventoryTransactionDTO);
        transaction.setInventory(inventory);
        transaction.setTransactionRef(generateTransactionRef());

        transaction = inventoryTransactionRepository.save(transaction);

        return inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(transaction);
    }

    @Override
    public List<InventoryTransactionDTO> viewTransactionHistory(Long inventoryId) {
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
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RecordNotFoundException("Transaction Not Found "+transactionId));

        return inventoryTransactionMapper.inventoryTransactionToInventoryTransactionDTO(inventoryTransaction);
    }


    private static String generateTransactionRef() {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "TXN-" + timestamp + "-" + uniquePart;
    }
}
