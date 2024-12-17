package com.example.demo.security;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginInfo {
    private String name;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginInfo(String name, String accessToken, String refreshToken) {
        this.name = name;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}