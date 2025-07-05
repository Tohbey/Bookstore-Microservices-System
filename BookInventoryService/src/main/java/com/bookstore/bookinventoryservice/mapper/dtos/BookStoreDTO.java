package com.bookstore.bookinventoryservice.mapper.dtos;

import com.bookstore.bookstorestarter.enums.StoreType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;


@Data
public class BookStoreDTO extends BaseDTO {

    private String name;

    private String description;

    @Embedded
    private Address address;

    private StoreType type = StoreType.PHYSICAL;

    private String contactEmail;
    private String contactPhone;

    @Data
    @Embeddable
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String country;
        private String postalCode;
    }
}
