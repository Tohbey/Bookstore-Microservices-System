package com.bookstore.authorservice.mapper.dtos;

import com.bookstore.authorservice.enums.Flag;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class BaseDTO {
    private Long id;

    private Flag flag;

    private String createdBy;

    private LocalDateTime createdDate;

    private String lastModifiedBy;

    private LocalDateTime lastModifiedDate;
}
