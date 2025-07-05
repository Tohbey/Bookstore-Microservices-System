package com.bookstore.bookstorestarter.dtos;

import lombok.Data;

@Data
public class PublishEvent {
    private BaseBookDTO bookDTO;
    private Integer publishedCopies;
    private Integer remainingCopies;
}
