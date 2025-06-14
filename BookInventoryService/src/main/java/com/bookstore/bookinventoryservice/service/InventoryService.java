package com.bookstore.bookinventoryservice.service;

import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;

import java.util.List;

public interface InventoryService {

    InventoryDTO createInventory(InventoryDTO inventoryDTO);

    InventoryDTO updateInventory(InventoryDTO inventoryDTO, Long inventoryId);

    InventoryDTO deleteInventory(Long inventoryId);

    InventoryDTO getInventoryById(Long inventoryId);

    List<InventoryDTO> getAllInventory(Flag flag, Long storeId, Long bookId);
}
