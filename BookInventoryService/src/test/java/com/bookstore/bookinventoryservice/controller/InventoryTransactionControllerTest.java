package com.bookstore.bookinventoryservice.controller;

import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.InventoryTransactionDTO;
import com.bookstore.bookinventoryservice.service.InventoryTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.bookstore.bookinventoryservice.mock.MockData.getInventoryTransactionDTOs;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryTransactionController.class)
class InventoryTransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InventoryTransactionService inventoryTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTransaction_shouldReturnSuccess() throws Exception {
        InventoryTransactionDTO requestDto = getInventoryTransactionDTOs().get(0);
        requestDto.setId(null);
        InventoryTransactionDTO responseDTO = getInventoryTransactionDTOs().get(0);

        when(inventoryTransactionService.create(requestDto)).thenReturn(responseDTO);

        mockMvc.perform(post(InventoryTransactionController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Transaction created successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.getId()));
    }

    @Test
    void createTransaction_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        //Given
        InventoryTransactionDTO requestDto = getInventoryTransactionDTOs().get(0);

        // Mock the service to throw an exception
        when(inventoryTransactionService.create(requestDto))
                .thenThrow(new RecordNotFoundException("Inventory Not Found "+requestDto.getId()));

        // When & Then
        mockMvc.perform(post(InventoryTransactionController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Inventory Not Found "+requestDto.getId()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void viewTransactionHistory() throws Exception {
        //Given
        Long inventoryId = 1L;
        List<InventoryTransactionDTO> responseDto = getInventoryTransactionDTOs();

        when(inventoryTransactionService.viewTransactionHistory(inventoryId))
                .thenReturn(responseDto);

        mockMvc.perform(get(InventoryTransactionController.BASE_URL+"/view-history/{inventoryId}", inventoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Transaction history retrieved successfully"))
                .andExpect(jsonPath("$.data.size()").value(responseDto.size()))
                .andExpect(jsonPath("$.data[0].id").value(responseDto.get(0).getId()))
                .andExpect(jsonPath("$.data[1].id").value(responseDto.get(1).getId()));
    }

    @Test
    void viewTransactionHistory_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long inventoryId = 1L;

        // Mock the service to throw an exception
        when(inventoryTransactionService.viewTransactionHistory(inventoryId))
                .thenThrow(new RecordNotFoundException("Inventory Not Found "+inventoryId));

        // When & Then
        mockMvc.perform(
                get(InventoryTransactionController.BASE_URL+"/view-history/{inventoryId}", inventoryId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Inventory Not Found "+inventoryId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getTransaction() throws Exception {
        Long transactionId = 1L;
        InventoryTransactionDTO requestDto = getInventoryTransactionDTOs().get(0);

        when(inventoryTransactionService.getTransaction(transactionId)).thenReturn(requestDto);

        mockMvc.perform(
                get(InventoryTransactionController.BASE_URL+"/{transactionId}", transactionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Transaction retrieved successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(transactionId));
    }

    @Test
    void getTransaction_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long transactionId = 1L;

        when(inventoryTransactionService.getTransaction(transactionId))
                .thenThrow(new RecordNotFoundException("Transaction Not Found "+transactionId));

        mockMvc.perform(
                        get(InventoryTransactionController.BASE_URL+"/{transactionId}", transactionId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Transaction Not Found "+transactionId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}