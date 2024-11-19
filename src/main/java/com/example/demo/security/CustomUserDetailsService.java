package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // TODO - db 조회해서 회원정보 가져오기

    @Override
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        return new Users(
                username,
                BCrypt.hashpw("1234",BCrypt.gensalt()),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
