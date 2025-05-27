package com.bookstore.authorservice.model;

import java.util.List;

public interface DataResponse<M> {
    boolean isValid();

    void setValid(boolean valid);

    List<M> getData();

    void setData(List<M> data);

    List<String> getMessages();

    void setMessages(List<String> messages);
}
