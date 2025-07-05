package com.bookstore.bookinventoryservice.mapper.dtos;

import com.bookstore.bookstorestarter.enums.InventoryStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryDTO extends BaseDTO {
    @NotBlank
    private Long bookId;

    @NotBlank
    private int totalCopies;

    private BookStoreDTO bookStore;

    @NotBlank
    private int availableCopies;

    @NotBlank
    private InventoryStatus status;

    private LocalDateTime lastRestockedAt;
}
