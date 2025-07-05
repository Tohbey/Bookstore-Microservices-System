package com.bookstore.authorservice.repository;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.entity.Book;
import com.bookstore.bookstorestarter.enums.Flag;
import com.bookstore.bookstorestarter.enums.Genre;
import com.bookstore.bookstorestarter.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    Author activeAuthor;
    Author activeAuthor2;
    @BeforeEach
    void setUp() {
        activeAuthor = new Author();
        activeAuthor.setFirstName("John");
        activeAuthor.setLastName("Doe");
        activeAuthor.setEmail("john@example.com");
        activeAuthor.setFlag(Flag.ENABLED);

        activeAuthor2 = new Author();
        activeAuthor2.setFirstName("Johnson");
        activeAuthor2.setLastName("Tems");
        activeAuthor2.setEmail("tems@example.com");
        activeAuthor2.setFlag(Flag.ENABLED);

        authorRepository.save(activeAuthor);
        authorRepository.save(activeAuthor2);

        Book book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("1234");
        book.setGenre(Genre.FANTASY);
        book.setStatus(Status.REVIEW);
        book.setEdition("Winter Edition");
        book.setFlag(Flag.ENABLED);
        book.setSuggestedRetailPrice(BigDecimal.ONE);
        book.setAuthors(List.of(activeAuthor));

        Book book1 = new Book();
        book1.setTitle("Book Title");
        book1.setIsbn("1234589");
        book1.setGenre(Genre.CHILDREN);
        book1.setStatus(Status.DRAFT);
        book1.setFlag(Flag.ENABLED);
        book1.setEdition("Summer Edition");
        book1.setSuggestedRetailPrice(BigDecimal.TEN);
        book1.setAuthors(List.of(activeAuthor2));

        bookRepository.save(book);
        bookRepository.save(book1);
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void findAllByAuthorsAndFlag() {
        List<Book> authorBooks = bookRepository.findAllByAuthorsAndFlag(List.of(activeAuthor), Flag.ENABLED);

        assertNotNull(authorBooks);
        assertThat(authorBooks.size()).isEqualTo(1);
        assertThat(authorBooks.get(0).getTitle()).isEqualTo("Book Title");
        assertThat(authorBooks.get(0).getIsbn()).isEqualTo("1234");
        assertThat(authorBooks.get(0).getGenre()).isEqualTo(Genre.FANTASY);
        assertThat(authorBooks.get(0).getStatus()).isEqualTo(Status.REVIEW);
        assertThat(authorBooks.get(0).getAuthors().size()).isEqualTo(1);
        assertThat(authorBooks.get(0).getAuthors().get(0)).isEqualTo(activeAuthor);
    }

    @Test
    void findAllByFlag() {
        List<Book> authorBooks = bookRepository.findAllByFlag(Flag.ENABLED);

        assertNotNull(authorBooks);
        assertThat(authorBooks.size()).isEqualTo(2);
        assertThat(authorBooks.get(0).getAuthors().get(0)).isEqualTo(activeAuthor);
        assertThat(authorBooks.get(0).getAuthors().size()).isEqualTo(1);
        assertThat(authorBooks.get(0).getTitle()).isEqualTo("Book Title");
        assertThat(authorBooks.get(0).getIsbn()).isEqualTo("1234");
        assertThat(authorBooks.get(0).getGenre()).isEqualTo(Genre.FANTASY);
        assertThat(authorBooks.get(0).getStatus()).isEqualTo(Status.REVIEW);
        assertThat(authorBooks.get(1).getAuthors().get(0)).isEqualTo(activeAuthor2);
        assertThat(authorBooks.get(1).getAuthors().size()).isEqualTo(1);
    }
}