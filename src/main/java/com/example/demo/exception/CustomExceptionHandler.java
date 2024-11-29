package com.example.demo.exception;

import com.example.demo.controller.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.demo.controller.ApiResult.ERROR;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ApiResult<?>> handleException(CustomException e) {
        log.error("Error occurs: {}", e.toString());
        return new ResponseEntity<>(ERROR(e.getErrorCode()), e.getErrorCode().getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResult<?>> applicationHandler(RuntimeException e) {
        log.error("Error occurs: {}", e.toString());
        return new ResponseEntity<>(ERROR(ErrorCode.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
