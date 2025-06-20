package com.bookstore.bookinventoryservice.dtos;

import com.bookstore.bookinventoryservice.mapper.dtos.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookDTO extends BaseDTO {
    private String title;
    private String synopsis;
    private LocalDateTime publishedAt;
    private String isbn;
    private String edition;
    private Integer totalCopies;
    private BigDecimal suggestedRetailPrice;
}
