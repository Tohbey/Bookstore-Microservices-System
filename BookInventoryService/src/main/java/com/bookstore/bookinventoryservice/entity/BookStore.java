package com.bookstore.bookinventoryservice.entity;

import com.bookstore.bookinventoryservice.entity.core.FlagableAuditableEntity;
import com.bookstore.bookinventoryservice.enums.StoreType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "BookStores")
public class BookStore extends FlagableAuditableEntity {

    private String name;

    @OneToMany(mappedBy = "bookStore")
    private List<Inventory> inventoryList = new ArrayList<>();

    private String description;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private StoreType type = StoreType.PHYSICAL;

    private String contactEmail;
    private String contactPhone;

    @Embeddable
    @Data
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String country;
        private String postalCode;
    }
}
