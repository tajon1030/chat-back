package com.example.demo.controller;

import com.example.demo.security.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/login")
    public ResponseEntity<LoginInfo> login(LoginRequestDTO request) {
        // username이랑 password로 검증한번은 필요함!
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // authenticate() 메서드를 실행할 때 미리 정의해 둔 CustomUserDetailsService의 loadUserByUsername()를 통해 유저에 대한 검증을 하고
        // 인증이 완료되면 Authentication객체를 리턴
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        // 어짜피 authenticationToken는 jwtFilter에서 만들어냄
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token으로 생성해 LoginInfo로 전달해줌
        String name = authenticationToken.getName();
        LoginInfo loginInfo = LoginInfo.builder().name(name).token(provider.generateToken(authentication)).build();

        // 추후 refresh token도 생성하여 저장한다.

        return ResponseEntity.ok()
                .body(loginInfo);
    }
}
