package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    private long tokenValidMin = 60; // 60분

    public String generateToken(Authentication authentication) {
        // 인증된 사용자의 권한 목록 조회
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .id(authentication.getName())
                .claim("roleNames", authorities)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))// 토큰발행일자
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(tokenValidMin).toInstant()))
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

    // JWT 토큰에서 사용자 인증 정보 가져오기
    public Authentication getAuthentication(String token) {
        List<SimpleGrantedAuthority> roles = Arrays.stream(getClaims(token).get("roleNames").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String username = getUsername(token);

        //https://velog.io/@sonaky47/Spring-Security-Jwt-토큰정보로-필터링-된-유저정보를-컨트롤러단에서-AuthenticationPricipal-어노테이션을-통해-가져오는법
        User principal = new User(username, "", roles);
        return new UsernamePasswordAuthenticationToken(principal, "", roles);
    }

    public boolean validateToken(String token) {
        return getClaims(token).getExpiration().after(Date.from(ZonedDateTime.now().toInstant()));
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "를 제외한 부분이 실제 토큰
        }
        return null;
    }
}