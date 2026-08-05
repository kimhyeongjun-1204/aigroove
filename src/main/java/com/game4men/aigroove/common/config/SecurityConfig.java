package com.game4men.aigroove.common.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .requestMatchers("/api/game/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css", "/*.ico", "/*.png", "/*.json").permitAll()
                .requestMatchers("/admin/**").authenticated()
                .anyRequest().permitAll()
            )
            // 인증 정보가 없는 요청은 403이 아니라 401로 응답한다.
            // (스프링 시큐리티 기본값은 Http403ForbiddenEntryPoint)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
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