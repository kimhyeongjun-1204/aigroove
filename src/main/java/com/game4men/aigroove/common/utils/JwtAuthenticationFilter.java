package com.game4men.aigroove.common.utils;

import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.LoginRepository;
import com.game4men.aigroove.common.repository.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 요청 헤더의 JWT를 검증해 SecurityContext에 인증 정보를 등록한다.
 * 접근 허용/거부는 판단하지 않는다 — 그것은 SecurityConfig의 책임이다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils,
                                   UserRepository userRepository,
                                   LoginRepository loginRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtUtils.validateToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String username = jwtUtils.getUsernameFromToken(token);

            // 토큰에는 username만 담겨 있어 소속을 알 수 없다.
            // 관리자(admin)를 먼저 조회하고, 없으면 게임 유저(user)에서 찾는다.
            Admin admin = loginRepository.findByUsername(username).orElse(null);
            if (admin != null) {
                String role = admin.getRole() != null
                        ? "ROLE_" + admin.getRole().name()
                        : "ROLE_ADMIN";
                setAuthentication(request, username, role);
                request.setAttribute("admin", admin);
            } else {
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    setAuthentication(request, username, "ROLE_USER");
                    request.setAttribute("user", user);
                }
            }
        }

        // 인증에 실패해도 여기서 응답을 만들지 않는다.
        // 인증 정보가 비어 있는 채로 넘기면 SecurityConfig가 거부 여부를 결정한다.
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, String username, String authority) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(authority)));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
