package com.bookstore.bookinventoryservice.mapper.mappers;

import com.bookstore.bookinventoryservice.entity.InventoryTransaction;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
    InventoryTransactionMapper INSTANCE = Mappers.getMapper(InventoryTransactionMapper.class);

    InventoryTransactionDTO inventoryTransactionToInventoryTransactionDTO(InventoryTransaction inventoryTransaction);

    InventoryTransaction inventoryTransactionDTOToInventoryTransaction(InventoryTransactionDTO inventoryTransactionDTO);
}
