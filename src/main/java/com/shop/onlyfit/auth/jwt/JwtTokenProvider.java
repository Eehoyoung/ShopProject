package com.shop.onlyfit.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.shop.onlyfit.domain.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {

    public String getTokenFromRequest(HttpServletRequest request) {
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
        return jwtHeader;
    }

    public Long getUserIdFromToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(token.replace(JwtProperties.TOKEN_PREFIX, "").trim())
                    .getClaim("id")
                    .asLong();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String createToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", user.getId())
                .withClaim("nickname", user.getName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
}
