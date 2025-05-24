package com.bookstore.authorservice.Util;

import com.bookstore.authorservice.exception.RecordAlreadyExistException;
import com.bookstore.authorservice.exception.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({RecordNotFoundException.class})
    public IDataResponse<Objects> handleRecordNotFoundException(RecordNotFoundException ex) {
        return new IDataResponse<Objects>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler({RecordAlreadyExistException.class})
    public IDataResponse<Objects> handleRecordAlreadyExistException(RecordAlreadyExistException ex) {
        return new IDataResponse<Objects>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public IDataResponse<Objects> handleRuntimeException(RuntimeException ex) {
        return new IDataResponse<Objects>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
