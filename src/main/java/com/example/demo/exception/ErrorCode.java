package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    KAKAO_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-ERR-500", "fail to get kakaoToken"),
    KAKAO_PROFILE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-ERR-500", "fail to get kakaoProfile"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "MEMBER-ERR-401", "Token is invalid"),
    NULL_REFRESH(HttpStatus.UNAUTHORIZED, "MEMBER-ERR-401", "Refresh Token is empty"),


    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "MEMBER-ERR-409", "email is duplicated"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-ERR-404", "user not founded"),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT-ERR-404", "chatRoom not founded"),
    ARCHIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARCHIVE-ERR-404", "archive not founded"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "MEMBER-ERR-401", "Permission is invalid"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-ERR-500", "Internal server error"),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}