package com.rafaelvincensi.gitrats.exception;

import com.rafaelvincensi.gitrats.config.exception.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDetails> handleConflictException(ConflictException ex){
        var errorDetails = new ErrorDetails(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException ex){
        var errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(GroupMaxCapacityException.class)
    public ResponseEntity<ErrorDetails> handleGroupMaxCapacityException(GroupMaxCapacityException ex){
        var errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
