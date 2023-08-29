package com.shop.onlyfit.auth.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.shop.onlyfit.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (isSwaggerPath(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtHeader = jwtTokenProvider.getTokenFromRequest(request);


        // header 가 정상 비정상 ?
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // jwt 토큰을 검증 정상 사용자 확인
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "").trim();

        Boolean isTokenRevokedOrExpired = redisService.isTokenExpired(token); // Redis 저장된 토큰 확인

        if (isTokenRevokedOrExpired) { // 전환된 토큰이거나 토큰 만료된 경우
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "전환된 토큰 또는 만료된 토큰입니다.");
            return;
        }


        Long userCode = null;
        try {
            userCode = jwtTokenProvider.getUserIdFromToken(jwtHeader);

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