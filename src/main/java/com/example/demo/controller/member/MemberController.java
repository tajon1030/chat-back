package com.example.demo.controller.member;

import com.example.demo.controller.ApiResult;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.LoginInfo;
import com.example.demo.security.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
    public ApiResult<LoginInfo> login(LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // authenticate()에서 CustomUserDetailsService의 loadUserByUsername()로 username/password 검증함
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        // 어짜피 authenticationToken는 jwtFilter에서 요청 들어올때마다 만들어냄
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token으로 LoginInfo 생성
        String name = authenticationToken.getName();
        LoginInfo loginInfo = LoginInfo.builder().name(name).token(provider.generateToken(authentication)).build();

        // TODO refresh token도 생성하여 저장한다.

        return ApiResult.OK(loginInfo);
    }
}
