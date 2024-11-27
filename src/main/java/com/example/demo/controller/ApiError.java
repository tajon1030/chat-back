package com.example.demo.controller;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
public class ApiError {
    // 오류메시지
    private final String message;

    // 오류코드
    private final int status;


    ApiError(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status);
    }

    ApiError(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }

}
