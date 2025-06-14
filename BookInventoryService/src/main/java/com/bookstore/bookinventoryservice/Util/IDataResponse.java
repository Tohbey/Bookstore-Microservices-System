package com.bookstore.bookinventoryservice.Util;

import com.bookstore.bookinventoryservice.model.DataResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IDataResponse<M> implements DataResponse<M> {

    private boolean valid;
    private List<M> data;
    private String message;

    public IDataResponse(boolean valid) {
        this.valid = valid;
    }

    public IDataResponse(HttpStatus status, String message){
        this.valid = status.is2xxSuccessful();
        this.message = message;
    }
}
