package com.example.demo.controller.member;

import com.example.demo.controller.ApiResult;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.security.LoginInfo;
import com.example.demo.security.LoginRequestDTO;
import com.example.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final JwtTokenProvider provider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 로그인
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ApiResult<LoginInfo> login(LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        // authenticate()에서 CustomUserDetailsService의 loadUserByUsername()로 username/password 검증함
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // token으로 LoginInfo 생성
        String name = authenticationToken.getName();
        LoginInfo loginInfo = LoginInfo.builder().name(name)
                .accessToken(provider.generateAccessToken(authentication))
                .refreshToken(provider.generateRefreshToken(authentication)).build();

        return ApiResult.OK(loginInfo);
    }

    /**
     * 토큰 재발행
     *
     * @param user
     * @param authHeader
     * @param refreshToken
     * @return
     */
    @RequestMapping("/refresh")
    public ApiResult<Map<String, String>> refresh(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestHeader("Authorization") String authHeader,
            String refreshToken
    ) {
        // refresh 토큰 검증
        if (!provider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        // refresh 토큰에 있는값과 로그인한 사용자값이 동일한지 검증
        Authentication authentication = provider.getAuthentication(refreshToken);
        if (authentication.getPrincipal() == SecurityContextHolder.getContext().getAuthentication().getPrincipal()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // accessToken 재발급
        String newAccessToken = provider.generateAccessToken(authentication);
        // Refresh Token의 유효기간이 1시간 미만일 경우 전체(Access,Refresh) 재발급
        String newRefreshToken = checkTime(provider.getClaims(refreshToken).getExpiration())
                ? provider.generateRefreshToken(authentication) : refreshToken;
        return ApiResult.OK(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
    }

    // 시간이 1시간 미만으로 남았을경우
    private boolean checkTime(Date exp) {
        long gap = exp.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;
    }

}
