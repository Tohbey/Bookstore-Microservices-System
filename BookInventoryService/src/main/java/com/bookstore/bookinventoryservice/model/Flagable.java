package com.bookstore.bookinventoryservice.model;


import com.bookstore.bookinventoryservice.enums.Flag;

public interface Flagable {
    Flag getFlag();

    void setFlag(Flag flag);
}
