package com.bookstore.bookinventoryservice.controller;

import com.bookstore.bookinventoryservice.enums.Flag;
import com.bookstore.bookinventoryservice.exception.RecordAlreadyExistException;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryDTO;
import com.bookstore.bookinventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.bookstore.bookinventoryservice.mock.MockData.getInventoryDTOs;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    InventoryService inventoryService;

    @Test
    void createInventory_shouldReturnSuccess() throws Exception {
        InventoryDTO requestDto = getInventoryDTOs().get(0);
        requestDto.setId(null);
        InventoryDTO responseDTO = getInventoryDTOs().get(0);

        when(inventoryService.createInventory(requestDto)).thenReturn(responseDTO);

        mockMvc.perform(post(InventoryController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Inventory created successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.getId()));
    }

    @Test
    void createInventory_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        InventoryDTO requestDto = getInventoryDTOs().get(0);
        requestDto.setId(null);

        when(inventoryService.createInventory(requestDto))
                .thenThrow(new RecordNotFoundException("Book Store Not Found "+requestDto.getBookStore().getId()));

        mockMvc.perform(post(InventoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Store Not Found "+requestDto.getBookStore().getId()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createInventory_shouldReturnAlreadyExist_whenServiceThrowsRecordAlreadyExistException() throws Exception {
        InventoryDTO requestDto = getInventoryDTOs().get(0);
        requestDto.setId(null);

        when(inventoryService.createInventory(requestDto))
                .thenThrow(new RecordAlreadyExistException("Inventory already exists for this book"));

        mockMvc.perform(post(InventoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Inventory already exists for this book"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateInventory_shouldReturnSuccess() throws Exception {
        InventoryDTO requestDto = getInventoryDTOs().get(0);
        Long id = requestDto.getId();
        InventoryDTO responseDTO = getInventoryDTOs().get(0);

        when(inventoryService.updateInventory(requestDto, id)).thenReturn(responseDTO);

        mockMvc.perform(put(InventoryController.BASE_URL +"/{inventoryId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Inventory updated successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.getId()));
    }

    @Test
    void updateInventory_shouldReturnAlreadyExist_whenServiceThrowsRecordAlreadyExistException() throws Exception {
        InventoryDTO requestDto = getInventoryDTOs().get(0);
        Long id = requestDto.getId();
        InventoryDTO responseDTO = getInventoryDTOs().get(0);

        when(inventoryService.updateInventory(requestDto, id))
                .thenThrow(new RecordNotFoundException("Inventory Not Found "+id));

        mockMvc.perform(put(InventoryController.BASE_URL +"/{inventoryId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Inventory Not Found "+id))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    void getInventoryById_shouldReturnSuccess() throws Exception {
        Long inventoryId = 1L;
        InventoryDTO requestDto = getInventoryDTOs().get(0);

        when(inventoryService.getInventoryById(inventoryId)).thenReturn(requestDto);

        mockMvc.perform(
                        get(InventoryController.BASE_URL+"/{inventoryId}", inventoryId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Inventory retrieved successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(inventoryId));
    }

    @Test
    void getInventoryById_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long inventoryId = 1L;

        when(inventoryService.getInventoryById(inventoryId))
                .thenThrow(new RecordNotFoundException("Inventory Not Found "+inventoryId));

        mockMvc.perform(
                        get(InventoryController.BASE_URL+"/{inventoryId}", inventoryId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Inventory Not Found "+inventoryId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getInventories_shouldReturnSuccess() throws Exception {
        Long storeId = 1L;
        Long bookId = 1L;
        List<InventoryDTO> responseDto = getInventoryDTOs();

        when(inventoryService.getAllInventory(Flag.ENABLED, storeId, bookId))
                .thenReturn(responseDto);

        mockMvc.perform(
                get(InventoryController.BASE_URL+"?storeId="+storeId+"&bookId="+bookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Inventories retrieved successfully"))
                .andExpect(jsonPath("$.data.size()").value(responseDto.size()))
                .andExpect(jsonPath("$.data[0].id").value(responseDto.get(0).getId()))
                .andExpect(jsonPath("$.data[1].id").value(responseDto.get(1).getId()));
    }

    @Test
    void getInventories_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long storeId = 1L;
        Long bookId = 1L;
        List<InventoryDTO> responseDto = getInventoryDTOs();

        when(inventoryService.getAllInventory(Flag.ENABLED, storeId, bookId))
                .thenThrow(new RecordNotFoundException("Book Store Not Found "+bookId));

        mockMvc.perform(
                        get(InventoryController.BASE_URL+"?storeId="+storeId+"&bookId="+bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Store Not Found "+bookId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteInventoryById_shouldReturnSuccess() throws Exception {
        Long inventoryId = 1L;
        InventoryDTO requestDto = getInventoryDTOs().get(0);

        when(inventoryService.deleteInventory(inventoryId)).thenReturn(requestDto);

        mockMvc.perform(
                        delete(InventoryController.BASE_URL+"/{inventoryId}", inventoryId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Inventory deleted successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(inventoryId));
    }

    @Test
    void deleteInventoryById_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long inventoryId = 1L;

        when(inventoryService.deleteInventory(inventoryId))
                .thenThrow(new RecordNotFoundException("Inventory Not Found "+inventoryId));

        mockMvc.perform(
                        delete(InventoryController.BASE_URL+"/{inventoryId}", inventoryId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Inventory Not Found "+inventoryId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}