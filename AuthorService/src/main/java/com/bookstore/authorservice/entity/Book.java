package com.bookstore.authorservice.entity;

import com.bookstore.authorservice.entity.core.FlagableAuditableEntity;
import com.bookstore.authorservice.enums.Genre;
import com.bookstore.authorservice.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Table(name = "books")
public class Book extends FlagableAuditableEntity {

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @NotBlank
    private String title;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(length = 2000)
    private String synopsis;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;
}
