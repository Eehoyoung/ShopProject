package com.shop.onlyfit.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shop.onlyfit.auth.jwt.JwtProperties;
import com.shop.onlyfit.auth.jwt.JwtTokenProvider;
import com.shop.onlyfit.domain.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisService redisService;
    private final JwtTokenProvider tokenProvider;
    private final UserServiceImpl userService;

    public void logoutToken(HttpServletRequest request, HttpServletResponse response) {

        String token = tokenProvider.getTokenFromRequest(request);

        Base64.Decoder decoder = Base64.getDecoder();
        final String[] splitJwt = Objects.requireNonNull(token).split("\\.");
        final String payloadStr = new String(decoder.decode(splitJwt[1].replace('-', '+' )
                .replace('_', '/' ).getBytes()));
        //base 64의 경우 "-"와 "_"가 없기 때문에 illegal base64 character 5f 에러 발생
        Long userId = getUserIdFromToken(payloadStr);
        Date expirationDate = getDateExpFromToken(payloadStr);
        redisService.saveToken(token, userId, expirationDate);
        clearCookie(JwtProperties.HEADER_STRING, request, response);
    }

    public Long getUserIdFromToken(String payloadStr) {
        JsonObject jsonObject = new Gson().fromJson(payloadStr, JsonObject.class);
        return jsonObject.get("id").getAsLong();
    }

    public Date getDateExpFromToken(String payloadStr) {
        JsonObject jsonObject = new Gson().fromJson(payloadStr, JsonObject.class);
        long exp = jsonObject.get("exp").getAsLong();
        return new Date(exp * 1000);
    }

    public void clearCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookie.setMaxAge(0); // 쿠키의 만료시간을 0으로 설정하여 삭제
                    cookie.setHttpOnly(true); // HttpOnly 설정
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

    public Cookie createJwtCookie(@NotNull User authenticatedUser, String jwtToken) {
        UserDetails userDetails = this.userService.loadUserByUsername(authenticatedUser.getLoginId());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        jwtToken = JwtProperties.TOKEN_PREFIX.trim() + jwtToken;
        Cookie jwtCookie = new Cookie("Authorization", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(JwtProperties.EXPIRATION_TIME);
        jwtCookie.setPath("/");

        return jwtCookie;
    }
}
