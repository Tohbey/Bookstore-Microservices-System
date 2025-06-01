package com.bookstore.authorservice.entity;

import com.bookstore.authorservice.entity.core.FlagableAuditableEntity;
import com.bookstore.authorservice.enums.Genre;
import com.bookstore.authorservice.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name = "books")
@Entity
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

    private LocalDateTime publishedAt;
    private String isbn;
    private String edition;
    private BigDecimal suggestedRetailPrice;
}
