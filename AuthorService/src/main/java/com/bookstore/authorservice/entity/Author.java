package com.bookstore.authorservice.entity;

import com.bookstore.authorservice.entity.core.FlagableAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Authors", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Author extends FlagableAuditableEntity {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Column(length = 1000)
    private String bio;

    @ManyToMany(mappedBy = "authors", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Book> books;
}