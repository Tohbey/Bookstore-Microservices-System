package com.bookstore.authorservice.mapper.dtos;

import com.bookstore.authorservice.enums.Flag;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseDTO {
    private Long id;

    private Flag flag;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;
}
