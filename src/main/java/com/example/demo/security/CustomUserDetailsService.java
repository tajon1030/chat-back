package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // TODO - db 조회해서 회원정보 가져오기

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new Users(
                "테스트",
                "1234",
                List.of("USER")
        );
    }
}
