package com.bookstore.authorservice.Util;

import com.bookstore.authorservice.model.DataResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IDataResponse<M> implements DataResponse<M> {

    private boolean valid;
    private List<M> data;
    private List<String> messages;

    public IDataResponse(boolean valid) {
        this.valid = valid;
    }

    public IDataResponse(HttpStatus status, String message){
        this.valid = status.is2xxSuccessful();
        this.messages = Collections.singletonList(message);
    }
}
