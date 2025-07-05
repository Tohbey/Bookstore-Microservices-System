package com.bookstore.bookinventoryservice.controller;

import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookinventoryservice.service.InventoryTransactionService;
import com.bookstore.bookstorestarter.Util.IDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(InventoryTransactionController.BASE_URL)
public class InventoryTransactionController {
    public static final String BASE_URL = "/api/inventory-transaction";

    private final InventoryTransactionService inventoryTransactionService;

    Logger logger = LoggerFactory.getLogger(InventoryTransactionController.class);

    public InventoryTransactionController(InventoryTransactionService inventoryTransactionService) {
        this.inventoryTransactionService = inventoryTransactionService;
    }

    @PostMapping
    public IDataResponse<InventoryTransactionDTO> createTransaction(@RequestBody InventoryTransactionDTO inventoryTransactionDTO) {
        logger.info("Creating transaction details {}", inventoryTransactionDTO);
        IDataResponse<InventoryTransactionDTO> response = new IDataResponse<>();
        response.setData(List.of(inventoryTransactionService.create(inventoryTransactionDTO)));
        response.setValid(true);
        response.setMessage("Transaction created successfully");
        return response;
    }

    @GetMapping(value = "view-history/{inventoryId}")
    public IDataResponse<InventoryTransactionDTO> viewTransactionHistory(@PathVariable Long inventoryId) {
        logger.info("Retrieving transaction history for inventory {}", inventoryId);
        IDataResponse<InventoryTransactionDTO> response = new IDataResponse<>();
        response.setData(inventoryTransactionService.viewTransactionHistory(inventoryId));
        response.setValid(true);
        response.setMessage("Transaction history retrieved successfully");
        return response;
    }

    @GetMapping(value = "{transactionId}")
    public IDataResponse<InventoryTransactionDTO> getTransaction(@PathVariable Long transactionId) {
        logger.info("Retrieving transaction detail by id {}", transactionId);
        IDataResponse<InventoryTransactionDTO> response = new IDataResponse<>();
        response.setData(List.of(inventoryTransactionService.getTransaction(transactionId)));
        response.setValid(true);
        response.setMessage("Transaction retrieved successfully");
        return response;
    }
}
