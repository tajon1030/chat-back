package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    private static final long TOKEN_VALID_MIN = 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 24 * 7;  // 7일

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, TOKEN_VALID_MIN);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String generateToken(Authentication authentication, long time) {
        // 인증된 사용자의 권한 목록 조회
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .id(authentication.getName())
                .claim("seq", principal.getSeq())
                .claim("email", principal.getEmail())
                .claim("roleNames", authorities)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))// 토큰발행일자
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaims(String jwt) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            return (Claims) Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(resolveToken(jwt))
                    .getPayload();
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalFormed");
            throw malformedJwtException;
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("Expired");
            throw expiredJwtException;
        } catch (InvalidClaimException invalidClaimException) {
            log.error("Invalid");
            throw invalidClaimException;
        } catch (JwtException jwtException) {
            log.error("JWTError");
            throw jwtException;
        } catch (Exception e) {
            log.error("Error");
            throw e;
        }
    }

    public String getUsername(String token) {
        return getClaims(token).getId();
    }

    public String getEmail(String token) {
        return String.valueOf(getClaims(token).get("email"));
    }

    public Long getSeq(String token) {
        return Long.parseLong(String.valueOf(getClaims(token).get("seq")));
    }

    // JWT 토큰에서 사용자 인증 정보 가져오기
    public Authentication getAuthentication(String token) {
        List<SimpleGrantedAuthority> roles = Arrays.stream(getClaims(token).get("roleNames").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //https://velog.io/@sonaky47/Spring-Security-Jwt-토큰정보로-필터링-된-유저정보를-컨트롤러단에서-AuthenticationPricipal-어노테이션을-통해-가져오는법
        UserDetailsImpl principal = new UserDetailsImpl(getSeq(token), getUsername(token), getEmail(token),"" ,roles);
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    public boolean validateToken(String token) {
        // 어짜피 만료가 되면 error throw이기때문에 boolean 리턴값은 오류가 아니면 무조건 true
        return getClaims(token).getExpiration().after(Date.from(ZonedDateTime.now().toInstant()));
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "를 제외한 부분이 실제 토큰
        }
        return null;
    }
}