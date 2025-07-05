package com.bookstore.authorservice.service;

import com.bookstore.authorservice.mapper.dtos.BookDTO;
import com.bookstore.bookstorestarter.dtos.PublishDto;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    BookDTO updateBook(BookDTO bookDTO, Long bookId);

    List<BookDTO> getAllBooks(List<Long> authorId);

    BookDTO getBookById(Long bookId);

    BookDTO publishBook(PublishDto<BookDTO> publishDto);

    BookDTO deleteBook(Long bookId);
}
