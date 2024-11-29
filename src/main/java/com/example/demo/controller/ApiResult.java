package com.example.demo.controller;

import com.example.demo.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class ApiResult<T> {
    // 성공/실패 여부
    private final boolean success;

    // success = true, 응답 내용
    private final T response;

    // success = false, 에러 내용
    private final ApiError error;

    public static <T> ApiResult<T> OK(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<?> ERROR(ErrorCode errorCode) {
        return new ApiResult<>(false, null, new ApiError(errorCode));
    }
}
