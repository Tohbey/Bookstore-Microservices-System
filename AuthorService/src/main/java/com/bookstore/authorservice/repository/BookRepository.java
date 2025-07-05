package com.bookstore.authorservice.repository;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.entity.Book;
import com.bookstore.bookstorestarter.enums.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByAuthorsAndFlag(List<Author> authors, Flag flag);

    List<Book> findAllByFlag(Flag flag);
}
