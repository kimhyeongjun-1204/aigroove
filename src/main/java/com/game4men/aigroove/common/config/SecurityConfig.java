package com.game4men.aigroove.common.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.game4men.aigroove.common.utils.JwtAuthenticationFilter;
import com.game4men.aigroove.common.utils.JwtUtils;
import com.game4men.aigroove.common.repository.LoginRepository;
import com.game4men.aigroove.common.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 보안 강화: JWT 필터를 SecurityFilterChain에 등록하고, admin API에 인증 요구
        // 기존: anyRequest().permitAll() → 모든 API 무인증 접근 가능 (보안 취약)
        // 개선: 로그인/회원가입/정적리소스 외 admin API는 JWT 인증 필수
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/admin/signup").permitAll()
                .requestMatchers("/api/game/user/login", "/api/game/user/signup").permitAll()
                .requestMatchers("/api/game/notice/**", "/api/game/ranking/**").permitAll()
                .requestMatchers("/api/game/**").authenticated()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css", "/*.ico", "/*.png", "/*.json").permitAll()
                // 관리자 역할별 API 영역 (프론트 Sidebar.js 메뉴 기준).
                // 규칙은 위에서부터 차례로 비교해 처음 맞는 하나만 적용된다.
                // 그래서 구체적인 경로를 /admin/** 보다 위에 둬야 한다. 아래로 내리면 /admin/** 에 먼저 걸려
                // ROLE_ADMIN 만 있으면 통과해 버린다.
                // 세부 역할은 ADMIN_ 접두사를 붙인다. Admin.Role.USER 를 ROLE_USER 로 주면
                // 게임 유저의 ROLE_USER 와 이름이 같아져 게임 유저도 사용자 관리 API를 통과한다.
                .requestMatchers("/admin/admin/**").hasRole("ADMIN_MASTER")
                .requestMatchers("/admin/user/**", "/admin/inquiry/**", "/admin/notice/**").hasAnyRole("ADMIN_MASTER", "ADMIN_USER")
                .requestMatchers("/admin/ai/**", "/admin/dataset/**").hasAnyRole("ADMIN_MASTER", "ADMIN_AI")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            // 인증 정보가 없는 요청을 두 갈래로 나눈다.
            // 프론트 라우트(/admin/users 등)와 API 경로가 둘 다 /admin/ 으로 시작하므로
            // URL만으로는 구분할 수 없고, 요청자가 무엇을 기대하는지로 갈라야 한다.
            //
            //   브라우저 페이지 요청(새로고침) : Accept: text/html,...
            //     → index.html 로 forward. React 가 부팅해 로그인 화면으로 보낸다.
            //       (401을 그대로 주면 index.html 이 안 내려가 React 가 실행되지 못한다)
            //   axios API 요청               : Accept: application/json, text/plain, */*
            //     → 401 유지. 프론트가 응답을 보고 처리한다.
            //
            // forward 는 필터 체인을 다시 타지 않으므로(기본 REQUEST 디스패치 전용) 루프가 없다.
            // 스프링 시큐리티 기본값은 Http403ForbiddenEntryPoint 라 401 지정이 필요하다.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    String accept = req.getHeader(HttpHeaders.ACCEPT);
                    if (accept != null && accept.contains(MediaType.TEXT_HTML_VALUE)) {
                        req.getRequestDispatcher("/index.html").forward(req, res);
                    } else {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                })
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtUtils, userRepository, loginRepository),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 클라이언트 접속 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}