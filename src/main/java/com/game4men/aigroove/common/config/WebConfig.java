package com.game4men.aigroove.common.config;
import com.game4men.aigroove.common.utils.JwtUtils;
import com.game4men.aigroove.common.utils.JwtAuthenticationFilter;
import com.game4men.aigroove.common.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtils, userRepository));
        registrationBean.addUrlPatterns("/api/game/*"); // 모든 API 엔드포인트에 적용
        registrationBean.setOrder(1); // 필터 순서 설정
        return registrationBean;
    }

    // @Override
    // public void addViewControllers(ViewControllerRegistry registry) {
    //     registry.addViewController("/**").setViewName("forward:/index.html");
    // }
}