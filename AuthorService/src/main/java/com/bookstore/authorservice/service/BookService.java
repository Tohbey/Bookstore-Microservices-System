package com.bookstore.authorservice.service;

import com.bookstore.authorservice.dtos.PublishDto;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.mapper.dtos.BookDTO;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    BookDTO updateBook(BookDTO bookDTO, Long bookId);

    List<BookDTO> getAllBooks(Flag flag, List<Long> authorId);

    BookDTO getBookById(Long bookId);

    BookDTO publishBook(PublishDto publishDto);
}
