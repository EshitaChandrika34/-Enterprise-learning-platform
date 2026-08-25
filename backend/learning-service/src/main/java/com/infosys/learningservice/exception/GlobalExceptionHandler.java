package com.infosys.learningservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(
            ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleResourceNotFound(
            ResourceNotFoundException exception) {

        Map<String, Object> error =
                new HashMap<>();


        error.put(
                "timestamp",
                LocalDateTime.now());

        error.put(
                "status",
                HttpStatus.NOT_FOUND.value());

        error.put(
                "error",
                "Not Found");

        error.put(
                "message",
                exception.getMessage());


        return new ResponseEntity<>(
                error,
                HttpStatus.NOT_FOUND);
    }


    // ==========================================
    // BUSINESS RULE ERRORS
    // ==========================================

    @ExceptionHandler(
            IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>>
    handleIllegalArgument(
            IllegalArgumentException exception) {

        Map<String, Object> error =
                new HashMap<>();


        error.put(
                "timestamp",
                LocalDateTime.now());

        error.put(
                "status",
                HttpStatus.BAD_REQUEST.value());

        error.put(
                "error",
                "Bad Request");

        error.put(
                "message",
                exception.getMessage());


        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>
    handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors =
                new HashMap<>();


        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()));


        return new ResponseEntity<>(
                errors,
                HttpStatus.BAD_REQUEST);
    }
}