package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    private long tokenValidMin = 60; // 60분

    public String generateToken(String name) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(secretKey.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Jwts.builder()
                .id(name)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))// 토큰발행일자
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(tokenValidMin).toInstant()))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String jwt) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes("UTF-8"));
            return (Claims) Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(jwt)
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
        } catch (UnsupportedEncodingException ex) {
            log.error("Error");
            throw new RuntimeException(ex);
        } catch (Exception e) {
            log.error("Error");
            throw e;
        }
    }

    public boolean validateToken(String token){
        return getClaims(token).getExpiration().before(Date.from(ZonedDateTime.now().toInstant()));
    }
}