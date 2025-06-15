package com.bookstore.bookinventoryservice.repository;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.entity.Inventory;
import com.bookstore.bookinventoryservice.enums.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllByBookIdAndFlag(Long bookId, Flag flag);

    List<Inventory> findAllByBookStoreAndFlag(BookStore bookStore, Flag flag);

    List<Inventory> findAllByBookStoreAndBookIdAndFlag(BookStore bookStore, Long bookId, Flag flag);

    List<Inventory> findAllByFlag(Flag flag);

    Optional<Inventory> findByBookIdAndFlag(Long bookId, Flag flag);
}
