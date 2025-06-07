package com.bookstore.authorservice.controller;

import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.exception.RecordAlreadyExistException;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.AuthorDTO;
import com.bookstore.authorservice.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.bookstore.authorservice.mock.MockData.getAuthorDTOs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAuthor_shouldReturnSuccess() throws Exception {
        AuthorDTO requestDTO = getAuthorDTOs().get(0);
        requestDTO.setId(null);
        AuthorDTO responseDTO = getAuthorDTOs().get(0);

        when(authorService.createAuthor(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(
                post(AuthorController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Author created successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.getId()));
    }

    @Test
    void updateAuthor_shouldReturnSuccess() throws Exception {
        Long authorId = 1L;
        String email = "test@test.com";
        AuthorDTO requestDTO = getAuthorDTOs().get(0);
        requestDTO.setEmail(email);
        AuthorDTO responseDTO = getAuthorDTOs().get(0);
        responseDTO.setEmail(email);

        when(authorService.updateAuthor(requestDTO, authorId)).thenReturn(responseDTO);

        mockMvc.perform(
                        put(AuthorController.BASE_URL+ "/{authorId}", authorId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Author updated successfully"))
                .andExpect(jsonPath("$.data[0].id").value(authorId))
                .andExpect(jsonPath("$.data[0].email").value(email));
    }

    @Test
    void getAuthorById_shouldReturnSuccess() throws Exception {
        Long authorId = 1L;
        AuthorDTO responseDTO = getAuthorDTOs().get(0);

        when(authorService.getAuthorById(authorId)).thenReturn(responseDTO);

        mockMvc.perform(
                        get(AuthorController.BASE_URL+"/{authorId}", authorId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Author retrieved successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(authorId));
    }

    @Test
    void getAuthors_shouldReturnSuccess() throws Exception {
        List<AuthorDTO> responseDTO = getAuthorDTOs();

        when(authorService.getAllAuthors()).thenReturn(responseDTO);

        mockMvc.perform(
                        get(AuthorController.BASE_URL)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Authors retrieved successfully"))
                .andExpect(jsonPath("$.data.size()").value(responseDTO.size()))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.get(0).getId()))
                .andExpect(jsonPath("$.data[1].id").value(responseDTO.get(1).getId()));
    }

    @Test
    void deleteAuthorById_shouldReturnSuccess() throws Exception {
        Long authorId = 1L;
        AuthorDTO responseDTO = getAuthorDTOs().get(0);
        responseDTO.setFlag(Flag.DISABLED);

        when(authorService.deleteAuthor(authorId)).thenReturn(responseDTO);

        mockMvc.perform(
                        delete(AuthorController.BASE_URL+"/{authorId}", authorId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Author deleted successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].flag").value(Flag.DISABLED.getName()));
    }

    @Test
    void createAuthor_shouldReturnConflict_whenServiceThrowsRecordAlreadyExistException() throws Exception {
        // Given
        AuthorDTO requestDto = getAuthorDTOs().get(0);
        requestDto.setEmail(null);

        // Mock the service to throw an exception
        when(authorService.createAuthor(any(AuthorDTO.class)))
                .thenThrow(new RecordAlreadyExistException("Author already exists"));

        // When & Then
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Author already exists"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateAuthor_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        // Given
        AuthorDTO requestDto  = getAuthorDTOs().get(0);

        // Mock the service to throw an exception
        when(authorService.updateAuthor(any(AuthorDTO.class), any(Long.class)))
                .thenThrow(new RecordNotFoundException("Author Not Found "+requestDto.getId()));

        // When & Then
        mockMvc.perform(put(AuthorController.BASE_URL+"/{authorId}", requestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Author Not Found "+requestDto.getId()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getAuthorById_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        // Given
        AuthorDTO requestDto  = getAuthorDTOs().get(0);

        // Mock the service to throw an exception
        when(authorService.getAuthorById(any(Long.class)))
                .thenThrow(new RecordNotFoundException("Author Not Found "+requestDto.getId()));

        // When & Then
        mockMvc.perform(get(AuthorController.BASE_URL+"/{authorId}", requestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Author Not Found "+requestDto.getId()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteAuthor_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        // Given
        AuthorDTO requestDto  = getAuthorDTOs().get(0);

        // Mock the service to throw an exception
        when(authorService.deleteAuthor(any(Long.class)))
                .thenThrow(new RecordNotFoundException("Author Not Found "+requestDto.getId()));

        // When & Then
        mockMvc.perform(delete(AuthorController.BASE_URL+"/{authorId}", requestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Author Not Found "+requestDto.getId()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}