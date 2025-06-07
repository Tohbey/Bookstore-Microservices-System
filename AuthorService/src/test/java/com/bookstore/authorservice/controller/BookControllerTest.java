package com.bookstore.authorservice.controller;

import com.bookstore.authorservice.dtos.PublishDto;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.authorservice.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.bookstore.authorservice.mock.MockData.getBookDTOs;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookService bookService;


    @Test
    void createBook_shouldReturnSuccess() throws Exception {
        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setId(null);

        BookDTO responseDTO = getBookDTOs().get(0);

        when(bookService.createBook(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(
                post(BookController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book created successfully"))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.getId()));
    }

    @Test
    void updateBook_shouldReturnSuccess() throws Exception {
        Long bookId = 1L;
        String title = "new title";

        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setTitle(title);

        BookDTO responseDTO = getBookDTOs().get(0);
        responseDTO.setTitle(title);

        when(bookService.updateBook(requestDTO, bookId)).thenReturn(responseDTO);

        mockMvc.perform(
                put(BookController.BASE_URL+"/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book updated successfully"))
                .andExpect(jsonPath("$.data[0].id").value(bookId))
                .andExpect(jsonPath("$.data[0].title").value(title));
    }

    @Test
    void getBookById_shouldReturnSuccess() throws Exception {
        Long bookId = 1L;
        BookDTO requestDTO = getBookDTOs().get(0);

        when(bookService.getBookById(bookId)).thenReturn(requestDTO);

        mockMvc.perform(
                get(BookController.BASE_URL+"/{bookId}", bookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(bookId));
    }

    @Test
    void getBooks_shouldReturnSuccess() throws Exception {
        List<BookDTO> responseDTO = getBookDTOs();

        when(bookService.getAllBooks(List.of())).thenReturn(responseDTO);

        mockMvc.perform(
                post(BookController.BASE_URL+"/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Books retrieved successfully"))
                .andExpect(jsonPath("$.data.size()").value(responseDTO.size()))
                .andExpect(jsonPath("$.data[0].id").value(responseDTO.get(0).getId()))
                .andExpect(jsonPath("$.data[1].id").value(responseDTO.get(1).getId()));
    }

    @Test
    void deleteBook_shouldReturnSuccess() throws Exception {
        Long bookId = 1L;
        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setFlag(Flag.DISABLED);


        when(bookService.deleteBook(bookId)).thenReturn(requestDTO);

        mockMvc.perform(
                        delete(BookController.BASE_URL+"/{bookId}", bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book deleted successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty())
                .andExpect(jsonPath("$.data[0].flag").value(Flag.DISABLED.getName()));
    }

    @Test
    void createBook_shouldReturnBadRequest_whenServiceThrowsIllegalArgumentException() throws Exception {
        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setId(null);
        requestDTO.setAuthors(List.of());

        BookDTO responseDTO = getBookDTOs().get(0);

        when(bookService.createBook(requestDTO))
                .thenThrow(new IllegalArgumentException("Author list cannot be empty"));

        mockMvc.perform(
                        post(BookController.BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Author list cannot be empty"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createBook_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setId(null);
        requestDTO.setAuthors(List.of());

        BookDTO responseDTO = getBookDTOs().get(0);

        when(bookService.createBook(requestDTO))
                .thenThrow(new RecordNotFoundException("Some authors were not found"));

        mockMvc.perform(
                        post(BookController.BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Some authors were not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateBook_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long bookId = 1L;
        String title = "new title";

        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setTitle(title);
        requestDTO.setAuthors(List.of());

        BookDTO responseDTO = getBookDTOs().get(0);
        responseDTO.setTitle(title);

        when(bookService.updateBook(requestDTO, bookId))
                .thenThrow(new RecordNotFoundException("Some authors were not found"));

        mockMvc.perform(
                        put(BookController.BASE_URL+"/{bookId}", bookId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Some authors were not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateBook_shouldReturnBadRequest_whenServiceThrowsIllegalArgumentException() throws Exception {
        Long bookId = 1L;
        String title = "new title";

        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setTitle(title);
        requestDTO.setAuthors(List.of());

        BookDTO responseDTO = getBookDTOs().get(0);
        responseDTO.setTitle(title);

        when(bookService.updateBook(requestDTO, bookId))
                .thenThrow(new IllegalArgumentException("Author list cannot be empty"));;

        mockMvc.perform(
                        put(BookController.BASE_URL+"/{bookId}", bookId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Author list cannot be empty"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getBookById_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long bookId = 1L;
        BookDTO requestDTO = getBookDTOs().get(0);

        when(bookService.getBookById(bookId))
                .thenThrow(new RecordNotFoundException("Book Not Found "+bookId));

        mockMvc.perform(
                        get(BookController.BASE_URL+"/{bookId}", bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Not Found "+bookId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteBook_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        Long bookId = 1L;
        BookDTO requestDTO = getBookDTOs().get(0);
        requestDTO.setFlag(Flag.DISABLED);


        when(bookService.deleteBook(bookId)).thenThrow(new RecordNotFoundException("Book Not Found "+bookId));

        mockMvc.perform(
                        delete(BookController.BASE_URL+"/{bookId}", bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Not Found "+bookId))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void publishBook_shouldReturnSuccess() throws Exception {
        PublishDto publishDto = new PublishDto();
        publishDto.setBookDTO(getBookDTOs().get(0));
        publishDto.setPublishedCopies(10);


        when(bookService.publishBook(publishDto)).thenReturn(getBookDTOs().get(0));

        mockMvc.perform(
                        post(BookController.BASE_URL+"/publish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(publishDto)))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Book published successfully"))
                .andExpect(jsonPath("$.data[0]").isNotEmpty());
    }

    @Test
    void publishBook_shouldReturnNotFound_whenServiceThrowsRecordNotFoundException() throws Exception {
        PublishDto publishDto = new PublishDto();
        publishDto.setBookDTO(getBookDTOs().get(0));
        publishDto.setPublishedCopies(10);


        when(bookService.publishBook(publishDto))
                .thenThrow(new RecordNotFoundException("Book Not Found "+publishDto.getBookDTO().getId()));

        mockMvc.perform(
                        post(BookController.BASE_URL+"/publish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(publishDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Book Not Found "+publishDto.getBookDTO().getId()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void publishBook_shouldReturnRuntime_whenServiceThrowsRuntimeException() throws Exception {
        PublishDto publishDto = new PublishDto();
        publishDto.setBookDTO(getBookDTOs().get(0));
        publishDto.setPublishedCopies(10);


        when(bookService.publishBook(publishDto))
                .thenThrow(new RuntimeException("Not enough copies available"));

        mockMvc.perform(
                        post(BookController.BASE_URL+"/publish")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(publishDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Not enough copies available"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}