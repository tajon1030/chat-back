package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    private long tokenValidMin = 60; // 60분

    public String generateToken(String name) {
        SecretKey key = null;
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .id(name)
                .claim("roleNames", List.of(""))
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))// 토큰발행일자
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(tokenValidMin).toInstant()))
                .signWith(key)
                .compact();
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

    public List<String> getRoleNames(String token) {
        return (List<String>) getClaims(token).get("roleNames");
    }

    // JWT 토큰에서 사용자 인증 정보 가져오기
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        List<SimpleGrantedAuthority> roles = getRoleNames(token).stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, "", roles);
    }

    public boolean validateToken(String token) {
        return !getClaims(token).isEmpty();//.getExpiration().before(Date.from(ZonedDateTime.now().toInstant()));
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "를 제외한 부분이 실제 토큰
        }
        return null;
    }
}