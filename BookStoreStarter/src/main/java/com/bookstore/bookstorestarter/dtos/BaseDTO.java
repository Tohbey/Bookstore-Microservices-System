package com.bookstore.bookstorestarter.dtos;

import com.bookstore.bookstorestarter.enums.Flag;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class BaseDTO {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Flag flag;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;
}
