package com.bookstore.authorservice.repository;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.enums.Flag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        Author activeAuthor = new Author();
        activeAuthor.setFirstName("John");
        activeAuthor.setLastName("Doe");
        activeAuthor.setEmail("john@example.com");
        activeAuthor.setFlag(Flag.ENABLED);

        Author activeAuthor2 = new Author();
        activeAuthor2.setFirstName("Johnson");
        activeAuthor2.setLastName("Tems");
        activeAuthor2.setEmail("tems@example.com");
        activeAuthor2.setFlag(Flag.ENABLED);

        Author inactiveAuthor = new Author();
        inactiveAuthor.setFirstName("Jane");
        inactiveAuthor.setLastName("Smith");
        inactiveAuthor.setEmail("jane@example.com");
        inactiveAuthor.setFlag(Flag.DISABLED);

        authorRepository.save(activeAuthor);
        authorRepository.save(activeAuthor2);
        authorRepository.save(inactiveAuthor);
    }

    @AfterEach
    void tearDown() {
        authorRepository.deleteAll();
    }

    @Test
    void findAllByFlag() {
        List<Author> activeAuthors = authorRepository.findAllByFlag(Flag.ENABLED);

        assertThat(activeAuthors.size()).isEqualTo(2);
        assertThat(activeAuthors.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByEmail() {
        Optional<Author> authorOptional = authorRepository.findByEmail("john@example.com");

        assertThat(authorOptional).isPresent();
        assertThat(authorOptional.get().getEmail()).isEqualTo("john@example.com");
        assertThat(authorOptional.get().getFlag()).isEqualTo(Flag.ENABLED);
    }
}