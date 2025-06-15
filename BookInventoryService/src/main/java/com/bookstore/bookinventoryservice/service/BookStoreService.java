package com.bookstore.bookinventoryservice.service;

import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;

import java.util.List;

public interface BookStoreService {

    BookStoreDTO createBookStore(BookStoreDTO bookStoreDTO);

    BookStoreDTO getBookStoreById(Long id);

    List<BookStoreDTO> getAllBookStores();

    BookStoreDTO updateBookStore(BookStoreDTO bookStoreDTO, Long bookId);

    BookStoreDTO deleteBookStore(Long bookId);
}
