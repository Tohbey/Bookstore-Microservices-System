package com.bookstore.authorservice.model;

import com.bookstore.authorservice.enums.Flag;

public interface Flagable {
    Flag getFlag();

    void setFlag(Flag flag);
}
