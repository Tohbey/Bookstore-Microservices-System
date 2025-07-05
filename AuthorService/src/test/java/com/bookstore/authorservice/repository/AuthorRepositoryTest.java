package com.bookstore.authorservice.repository;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.bookstorestarter.enums.Flag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;
    private Author author1;
    private Author author2;

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

        author1 = authorRepository.save(activeAuthor);
        author2 = authorRepository.save(activeAuthor2);
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

    @Test
    void findAllByIdIn(){
        List<Long> authorIds = Arrays.asList(author1.getId(), author2.getId());
        List<Author> authors = authorRepository.findAllByIdIn(authorIds);

        assertThat(authors.size()).isEqualTo(2);
        assertThat(authors.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(authors.get(1).getEmail()).isEqualTo("tems@example.com");
    }
}