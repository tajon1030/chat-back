package com.example.demo.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Users extends User {

    private String id;
    private String pw;
    private List<String> roleNames = new ArrayList<>();

    public Users(String username, String password, List<String> roleNames) {
        super(username, password, roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str))
                .collect(Collectors.toList()));
        this.id = username;
        this.pw = password;
        this.roleNames = roleNames;
    }
}
