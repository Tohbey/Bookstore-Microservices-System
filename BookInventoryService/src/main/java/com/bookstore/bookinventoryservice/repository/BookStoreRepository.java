package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookstorestarter.enums.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookStoreRepository extends JpaRepository<BookStore, Long> {

    Optional<BookStore> findByName(String name);

    List<BookStore> findAllByFlag(Flag flag);
}
