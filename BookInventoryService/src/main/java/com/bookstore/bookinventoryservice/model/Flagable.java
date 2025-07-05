package com.bookstore.bookinventoryservice.model;


import com.bookstore.bookstorestarter.enums.Flag;

public interface Flagable {
    Flag getFlag();

    void setFlag(Flag flag);
}
