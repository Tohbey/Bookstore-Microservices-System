package com.bookstore.authorservice.mapper.dtos;

import com.bookstore.authorservice.enums.Genre;
import com.bookstore.authorservice.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookDTO extends BaseDTO {
    private String title;
    private Genre genre;
    private String synopsis;
    private Status status;
    private List<AuthorDTO> authors;
    private LocalDateTime publishedAt;
    private String isbn;
    private String edition;
    private BigDecimal suggestedRetailPrice;
}
