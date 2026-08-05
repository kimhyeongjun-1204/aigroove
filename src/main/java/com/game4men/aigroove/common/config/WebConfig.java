package com.game4men.aigroove.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * JwtAuthenticationFilter는 SecurityConfig의 SecurityFilterChain에만 등록한다.
 * 여기서 FilterRegistrationBean으로 중복 등록하면 시큐리티 체인 밖에도 같은 필터가
 * 붙어, 인가 규칙과 무관하게 동작하는 경로가 생긴다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    //     registry.addViewController("/**").setViewName("forward:/index.html");
    // }
}
