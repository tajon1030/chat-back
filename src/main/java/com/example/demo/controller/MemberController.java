package com.example.demo.controller;

import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.LoginInfo;
import com.example.demo.security.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final JwtTokenProvider provider;

    @PostMapping("/login")
    public ResponseEntity<LoginInfo> login(LoginRequestDTO request) {
        // 시큐리티로그인 + jwt토큰 생성하여 리턴값으로 전달해줌
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 로그인회원 id를 token으로 생성해 LoginInfo로 전달해줌
        String name = authenticationToken.getName();
        LoginInfo loginInfo = LoginInfo.builder().name(name).token(provider.generateToken(name)).build();
        return ResponseEntity.ok()
                .body(loginInfo);
    }
}
