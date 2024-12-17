package com.example.demo.security;

import com.example.demo.controller.ApiResult;
import com.example.demo.exception.ErrorCode;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // TODO https://chaewsscode.tistory.com/234 이거보고 리팩토링하기
        if (request.getRequestURI().startsWith("/member/login")
                || request.getRequestURI().startsWith("/ws-stomp")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) { // 토큰 만료 체크
                String username = jwtTokenProvider.getUsername(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 토큰으로 인증객체 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response); // 필터 체인 진행
        } catch (Exception e) {
            log.error("JWT Check Error ---------------");
            log.error(e.getMessage());

            Gson gson = new Gson();
            ApiResult<?> error = ApiResult.ERROR(ErrorCode.INVALID_TOKEN);
            String msg = gson.toJson(error);

            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.println(msg);
            pw.close();
        }
    }
}
