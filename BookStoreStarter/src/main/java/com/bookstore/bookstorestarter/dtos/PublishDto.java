package com.bookstore.bookstorestarter.dtos;

import lombok.Data;

@Data
public class PublishDto<M extends BaseBookDTO> {
    private M bookDTO;
    private Integer publishedCopies;
    private Integer remainingCopies;
}
