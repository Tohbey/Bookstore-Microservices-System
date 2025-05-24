package com.bookstore.authorservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @NotBlank
    private String title;

    private String genre;

    @Column(length = 2000)
    private String synopsis;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    public enum Status {
        DRAFT,
        PUBLISHED
    }
}
