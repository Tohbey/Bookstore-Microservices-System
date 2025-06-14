package com.bookstore.bookinventoryservice.Util;

import com.bookstore.bookinventoryservice.exception.RecordAlreadyExistException;
import com.bookstore.bookinventoryservice.exception.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<IDataResponse<?>> handleRecordNotFoundException(RecordNotFoundException ex) {
        IDataResponse<?> response = new IDataResponse<>(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RecordAlreadyExistException.class)
    public ResponseEntity<IDataResponse<?>> handleRecordAlreadyExistException(RecordAlreadyExistException ex) {
        IDataResponse<?> response = new IDataResponse<>(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<IDataResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        IDataResponse<?> response = new IDataResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<IDataResponse<?>> handleRuntimeException(RuntimeException ex) {
        IDataResponse<?> response = new IDataResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

