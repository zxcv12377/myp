package com.example.server.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            return refreshToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = resolveRefreshToken(request);
        if (accessToken != null && accessToken.startsWith("Bearer")) {
            String token = accessToken.substring(7);
            try {
                if (jwtProvider.validateToken(token)) {
                    Authentication auth = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (ExpiredJwtException e) {
                // 1) 만료 예외 잡기
                if (refreshToken != null) {
                    try {
                        String email = jwtProvider.getEmail(refreshToken);
                        String newAccessToken = jwtProvider.createAccessToken(email);

                        response.setHeader("Authorization", "Bearer " + newAccessToken);

                        Authentication auth = jwtProvider.getAuthentication(newAccessToken);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } catch (Exception err) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                "Refresh token이 유효하지 않습니다. 다시 로그인해주세요.");
                        return;
                    }
                } else {
                    // 리프레시도 만료/무효면 401 리턴
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Access token이 만료되었고, Refresh token이 없습니다. 다시 로그인해주세요.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다: " + e.getMessage());
                return; // 필터 체인 중단
            }
        }
        filterChain.doFilter(request, response);
    }

}
