package com.example.demo.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

public class Users extends User {

    private String id;
    private String pw;
    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    public Users(String username, String password, List<SimpleGrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = username;
        this.pw = password;
        this.authorities = authorities;
    }
}
