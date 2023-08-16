package com.shop.onlyfit.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (isSwaggerPath(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwtHeader = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JwtProperties.HEADER_STRING.equals(cookie.getName())) {
                    jwtHeader = cookie.getValue();
                    break;
                }
            }
        }

        // header 가 정상 비정상 ?
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // jwt 토큰을 검증 정상 사용자 확인
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "").trim();

        Long userCode = null;

        try {
            userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                    .getClaim("id").asLong();

        } catch (TokenExpiredException e) {
            e.getStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰 만료");
        } catch (JWTVerificationException e) {
            e.getStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효 않는 토큰.");
        }

        request.setAttribute("userCode", userCode);

        filterChain.doFilter(request, response);
    }

    private boolean isSwaggerPath(String path) {
        String[] swaggerPaths = {
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/v2/api-docs/**",
                "/webjars/**",
                "/main/login"
        };

        return Arrays.stream(swaggerPaths).anyMatch(path::startsWith);
    }
}