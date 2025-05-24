package com.bookstore.authorservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "Authors", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Author {

    @Id
    @GeneratedValue
    private UUID id;

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