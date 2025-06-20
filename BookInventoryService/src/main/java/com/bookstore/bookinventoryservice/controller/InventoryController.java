package com.bookstore.bookinventoryservice.controller;

import com.bookstore.bookinventoryservice.Util.IDataResponse;
import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;
import com.bookstore.bookinventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(InventoryController.BASE_URL)
public class InventoryController {
    public static final String BASE_URL = "/api/inventory";

    private final InventoryService inventoryService;

    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public IDataResponse<InventoryDTO> createInventory(@RequestBody InventoryDTO inventoryDTO) {
        logger.info("Creating new inventory {}", inventoryDTO);
        IDataResponse<InventoryDTO> response = new IDataResponse<>();
        response.setData(List.of(inventoryService.createInventory(inventoryDTO)));
        response.setValid(true);
        response.setMessage("Inventory created successfully");
        return response;
    }

    @PutMapping(value = "{inventoryId}")
    public IDataResponse<InventoryDTO> updateInventory(@RequestBody InventoryDTO inventoryDTO, @PathVariable Long inventoryId) {
        logger.info("Updating inventory details {}", inventoryDTO);
        IDataResponse<InventoryDTO> response = new IDataResponse<>();
        response.setData(List.of(inventoryService.updateInventory(inventoryDTO, inventoryId)));
        response.setValid(true);
        response.setMessage("Inventory updated successfully");
        return response;
    }

    @GetMapping(value = "{inventoryId}")
    public IDataResponse<InventoryDTO> getInventoryById(@PathVariable Long inventoryId) {
        logger.info("Getting inventory details {}", inventoryId);
        IDataResponse<InventoryDTO> response = new IDataResponse<>();
        response.setData(List.of(inventoryService.getInventoryById(inventoryId)));
        response.setValid(true);
        response.setMessage("Inventory retrieved successfully");
        return response;
    }

    @GetMapping()
    public IDataResponse<InventoryDTO> getInventories(@RequestParam(required = false) Long storeId,
                                                      @RequestParam(required = false) Long bookId) {
        logger.info("Getting inventories details by {} or  {}", storeId, bookId);
        IDataResponse<InventoryDTO> response = new IDataResponse<>();
        response.setData(inventoryService.getAllInventory(Flag.ENABLED, storeId, bookId));
        response.setValid(true);
        response.setMessage("Inventories retrieved successfully");
        return response;
    }

    @DeleteMapping(value = "{inventoryId}")
    public IDataResponse<InventoryDTO> deleteInventoryById(@PathVariable Long inventoryId) {
        logger.info("Deleting inventory details {}", inventoryId);
        IDataResponse<InventoryDTO> response = new IDataResponse<>();
        response.setData(List.of(inventoryService.deleteInventory(inventoryId)));
        response.setValid(true);
        response.setMessage("Inventory deleted successfully");
        return response;
    }
}
