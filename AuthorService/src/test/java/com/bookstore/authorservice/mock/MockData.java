package com.bookstore.authorservice.mock;

import com.bookstore.authorservice.entity.Author;
import com.bookstore.authorservice.entity.Book;
import com.bookstore.authorservice.enums.Flag;
import com.bookstore.authorservice.enums.Genre;
import com.bookstore.authorservice.enums.Status;
import com.bookstore.authorservice.mapper.dtos.AuthorDTO;
import com.bookstore.authorservice.mapper.dtos.BookDTO;

import java.util.List;

public class MockData {

    public static List<Author> getAuthors() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("john.doe@gmail.com");
        author.setFlag(Flag.ENABLED);

        Author author1 = new Author();
        author1.setId(2L);
        author1.setFirstName("Johnson");
        author1.setLastName("Does");
        author1.setEmail("johnson.doe@gmail.com");
        author1.setFlag(Flag.ENABLED);

        return List.of(author, author1);
    }

    public static List<AuthorDTO> getAuthorDTOs() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@gmail.com");
        authorDTO.setFlag(Flag.ENABLED);

        AuthorDTO authorDTO1 = new AuthorDTO();
        authorDTO1.setId(2L);
        authorDTO1.setFirstName("Johnson");
        authorDTO1.setLastName("Does");
        authorDTO1.setEmail("johnson.doe@gmail.com");
        authorDTO1.setFlag(Flag.ENABLED);

        return List.of(authorDTO, authorDTO1);
    }

    public static List<Book> getBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setFlag(Flag.ENABLED);
        book.setStatus(Status.REVIEW);
        book.setTitle("Title");
        book.setGenre(Genre.FANTASY);
        book.setSynopsis("Synopsis");
        book.setAuthors(List.of(getAuthors().get(0)));

        Book book1 = new Book();
        book1.setId(2L);
        book1.setFlag(Flag.ENABLED);
        book1.setStatus(Status.DRAFT);
        book1.setTitle("Songs");
        book1.setGenre(Genre.CHILDREN);
        book1.setSynopsis("QPEO");
        book1.setAuthors(List.of(getAuthors().get(1)));

        return List.of(book, book1);
    }

    public static List<BookDTO> getBookDTOs() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setFlag(Flag.ENABLED);
        bookDTO.setStatus(Status.REVIEW);
        bookDTO.setTitle("Title");
        bookDTO.setGenre(Genre.FANTASY);
        bookDTO.setSynopsis("Synopsis");
        bookDTO.setAuthors(List.of(getAuthorDTOs().get(0)));

        BookDTO bookDTO1 = new BookDTO();
        bookDTO1.setId(2L);
        bookDTO1.setFlag(Flag.ENABLED);
        bookDTO1.setStatus(Status.DRAFT);
        bookDTO1.setTitle("Songs");
        bookDTO1.setGenre(Genre.CHILDREN);
        bookDTO1.setSynopsis("QPEO");
        bookDTO1.setAuthors(List.of(getAuthorDTOs().get(1)));

        return List.of(bookDTO, bookDTO1);
    }
}
