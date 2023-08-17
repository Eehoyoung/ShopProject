package com.shop.onlyfit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveToken(String token, Long userId, Date expirationDate) {
        redisTemplate.opsForValue().set(token, userId);
        redisTemplate.expireAt(token, expirationDate);
    }

    public void revokeToken(String token) {
        redisTemplate.delete(token);
    }

    public Boolean isTokenExpired(String token) {
        return Boolean.FALSE.equals(redisTemplate.hasKey(token));
    }
}
