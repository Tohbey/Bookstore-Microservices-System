package com.bookstore.authorservice.dtos;

import com.bookstore.authorservice.mapper.dtos.BookDTO;
import lombok.Data;

@Data
public class PublishDto {
    private BookDTO bookDTO;
    private Integer publishedCopies;
    private Integer remainingCopies;
}
