package com.bookstore.bookinventoryservice.entity.core;

import com.bookstore.bookinventoryservice.model.Model;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity implements Model, Serializable {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;

    public static Long defaultIdOf(Long id, BaseEntity entity) {
        return id != null ? id : (entity != null ? entity.getId() : null);
    }
}
