package com.shop.onlyfit.config;

import com.shop.onlyfit.auth.jwt.JwtAuthenticationEntryPoint;
import com.shop.onlyfit.auth.jwt.JwtRequestFilter;
import com.shop.onlyfit.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // JwtTokenProvider를 주입하도록 수정

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .ignoringAntMatchers("/ws/**", "/room") // Add this line to disable CSRF for WebSocket connections.
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/image/**")
                .permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/market/**").hasRole("SELLER")
                .antMatchers("/main/order", "/main/profile", "/main/mileage", "/main/address",
                        "/main/cart", "/main/payment", "/main/product/basketadd_ok")
                .authenticated()
                .antMatchers("/main/index", "/main/category/**", "/main/product/**", "/auth/**", "/ws/*", "/pub/chat/**", "/sub/chat/**")
                .permitAll()

                // update this line to permit all for /ws/*, /pub/chat and /sub/chat/*
                .and()
                .exceptionHandling().accessDeniedPage("/main/restrict")
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // JWT 인증 에러 핸들링
                // JWT 필터 추가
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
