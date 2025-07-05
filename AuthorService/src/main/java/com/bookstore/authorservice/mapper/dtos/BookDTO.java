package com.bookstore.authorservice.mapper.dtos;

import com.bookstore.bookstorestarter.dtos.BaseBookDTO;
import com.bookstore.bookstorestarter.enums.Genre;
import com.bookstore.bookstorestarter.enums.Status;
import lombok.Data;

import java.util.List;

@Data
public class BookDTO extends BaseBookDTO {
    private Genre genre;
    private Status status;
    private List<AuthorDTO> authors;
}
