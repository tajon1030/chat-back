package com.example.demo.exception;

import com.example.demo.controller.ApiResult;
import io.jsonwebtoken.ExpiredJwtException;
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
    public ApiResult<?> handleException(CustomException e) {
        log.error("Error occurs: {}", e.toString());
        return ApiResult.ERROR(e.getErrorCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> applicationHandler(RuntimeException e) {
        log.error("Error occurs: {}", e.toString());
        return ApiResult.ERROR(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ApiResult<?> handleException(ExpiredJwtException e) {
        log.error("Error occurs: {}", e.toString());
        return ApiResult.ERROR(ErrorCode.INVALID_TOKEN);
    }

}
