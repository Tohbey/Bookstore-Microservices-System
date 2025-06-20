package com.bookstore.bookinventoryservice.controller;

import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import com.bookstore.bookinventoryservice.service.BookStoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.bookstore.bookinventoryservice.mock.MockData.getBookStoreDTOs;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;


@WebMvcTest(BookStoreController.class)
class BookStoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookStoreService bookStoreService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBookStore_shouldReturnSuccess() throws Exception {
        BookStoreDTO requestBookStoreDTO = getBookStoreDTOs().get(0);
        requestBookStoreDTO.setId(null);
        BookStoreDTO responseBookStoreDTO = getBookStoreDTOs().get(0);


        when(bookStoreService.createBookStore(requestBookStoreDTO)).thenReturn(responseBookStoreDTO);

        mockMvc.perform(post(BookStoreController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookStoreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book Store created successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseBookStoreDTO.getId()))
                .andExpect(jsonPath("$.data[0].name").value(responseBookStoreDTO.getName()));
    }


    @Test
    void createBookStore_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        BookStoreDTO requestBookStoreDTO = getBookStoreDTOs().get(0);
        requestBookStoreDTO.setId(null);


        when(bookStoreService.createBookStore(requestBookStoreDTO))
                .thenThrow(new RecordNotFoundException("Book Store already exists with name " + requestBookStoreDTO.getName()));

        mockMvc.perform(post(BookStoreController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookStoreDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Store already exists with name " + requestBookStoreDTO.getName()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    void updateBookStore_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        BookStoreDTO requestBookStoreDTO = getBookStoreDTOs().get(0);
        Long id = requestBookStoreDTO.getId();

        when(bookStoreService.updateBookStore(requestBookStoreDTO, id))
                .thenThrow(new RecordNotFoundException("Book Store Not Found " +id));

        mockMvc.perform(put(BookStoreController.BASE_URL+"/{bookStoreId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookStoreDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Store Not Found " +id))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateBookStore_shouldReturnSuccess() throws Exception {
        BookStoreDTO requestBookStoreDTO = getBookStoreDTOs().get(0);
        Long id = requestBookStoreDTO.getId();
        BookStoreDTO responseBookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreService.updateBookStore(requestBookStoreDTO, id)).thenReturn(responseBookStoreDTO);

        mockMvc.perform(put(BookStoreController.BASE_URL+"/{bookStoreId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookStoreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book Store updated successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseBookStoreDTO.getId()));
    }

    @Test
    void getBookStoreById_shouldReturnSuccess() throws Exception {
        Long id = 1L;
        BookStoreDTO responseBookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreService.getBookStoreById(id)).thenReturn(responseBookStoreDTO);

        mockMvc.perform(get(BookStoreController.BASE_URL+"/{bookStoreId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book Store retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(id));
    }

    @Test
    void getBookStoreById_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long id = 1L;

        when(bookStoreService.getBookStoreById(id))
                .thenThrow(new RecordNotFoundException("Book Store Not Found " +id));

        mockMvc.perform(get(BookStoreController.BASE_URL+"/{bookStoreId}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Store Not Found " +id))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getAllBookStores() throws Exception {
        List<BookStoreDTO> responseBookStoreDTOs = getBookStoreDTOs();

        when(bookStoreService.getAllBookStores()).thenReturn(responseBookStoreDTOs);

        mockMvc.perform(get(BookStoreController.BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book stores retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseBookStoreDTOs.get(0).getId()));
    }

    @Test
    void deleteBookStoreById_shouldReturnSuccess() throws Exception {
        Long id = 1L;
        BookStoreDTO responseBookStoreDTO = getBookStoreDTOs().get(0);

        when(bookStoreService.deleteBookStore(id)).thenReturn(responseBookStoreDTO);

        mockMvc.perform(delete(BookStoreController.BASE_URL+"/{bookStoreId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book Store deleted successfully"))
                .andExpect(jsonPath("$.data[0].id").value(id));
    }

    @Test
    void deleteInventoryById_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long id = 1L;

        when(bookStoreService.deleteBookStore(id))
                .thenThrow(new RecordNotFoundException("Book Store Not Found "+id));

        mockMvc.perform(
                        delete(BookStoreController.BASE_URL+"/{bookStoreId}", id)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Store Not Found "+id))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}