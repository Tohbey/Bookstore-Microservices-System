package com.bookstore.authorservice.repository;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.bookstorestarter.enums.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByFlag(Flag flag);

    Optional<Author> findByEmail(String email);

    List<Author> findAllByIdIn(List<Long> ids);
}
