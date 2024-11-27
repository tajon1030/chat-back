package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.example.demo.controller.ApiResult.ERROR;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResult<?>> handleNotFoundException(Exception e) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(ERROR(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

}
