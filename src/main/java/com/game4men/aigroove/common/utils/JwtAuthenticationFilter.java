package com.game4men.aigroove.common.utils;

import com.game4men.aigroove.common.repository.UserRepository;
import com.game4men.aigroove.common.entity.User;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 인증이 필요 없는 경로는 건너뛰기
        if (shouldSkipAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 토큰 추출
        String token = extractToken(request);

        if (token != null && jwtUtils.validateToken(token)) {
            // 토큰에서 사용자 이름 추출
            String username = jwtUtils.getUsernameFromToken(token);

            // 사용자 정보 조회
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user != null) {
                // 사용자 정보를 요청 속성에 설정
                request.setAttribute("user", user);

                // 요청 컨텍스트에 인증 정보 설정 (선택사항)
                // setAuthenticationContext(request, user);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("User not found");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or missing token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipAuthentication(HttpServletRequest request) {
        // 로그인, 회원가입 등 인증이 필요 없는 경로 설정
        String path = request.getRequestURI();
        return path.startsWith("/api/game/user/login") ||
                path.startsWith("/api/game/user/signup") ||
                path.startsWith("/api/game/notice") ||
                path.startsWith("/api/game/ranking") ||
                path.startsWith("/admin");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // private void setAuthenticationContext(HttpServletRequest request, User user)
    // {
    // // 사용자 컨텍스트를 저장하는 방법 (ThreadLocal 등을 사용할 수 있음)
    // UserContext.setCurrentUser(user);
    // }
}