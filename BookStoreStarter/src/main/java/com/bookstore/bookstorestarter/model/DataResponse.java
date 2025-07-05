package com.bookstore.bookstorestarter.model;

import java.util.List;

public interface DataResponse<M> {
    boolean isValid();

    void setValid(boolean valid);

    List<M> getData();

    void setData(List<M> data);

    String getMessage();

    void setMessage(String message);
}
