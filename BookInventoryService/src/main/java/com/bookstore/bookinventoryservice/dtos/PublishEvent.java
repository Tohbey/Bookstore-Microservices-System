package com.bookstore.bookinventoryservice.dtos;

import lombok.Data;

@Data
public class PublishEvent {
    private BookDTO bookDTO;
    private Integer publishedCopies;
    private Integer remainingCopies;
}
