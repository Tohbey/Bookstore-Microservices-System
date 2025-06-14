package com.bookstore.bookinventoryservice.mapper.mappers;

import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    InventoryDTO InventoryToInventoryDTO(Inventory author);

    Inventory inventoryDTOToInventory(InventoryDTO authorDTO);
}
