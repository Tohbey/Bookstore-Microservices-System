package com.bookstore.authorservice.mapper.dtos;

import com.bookstore.authorservice.enums.Genre;
import com.bookstore.authorservice.enums.Status;
import lombok.Data;

import java.util.List;

@Data
public class BookDTO extends BaseDTO {
    private String title;
    private Genre genre;
    private String synopsis;
    private Status status;
    private List<AuthorDTO> authors;
}
