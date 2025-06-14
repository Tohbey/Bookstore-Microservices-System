package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.BookStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookStoreRepository extends JpaRepository<BookStore, Long> {
}
