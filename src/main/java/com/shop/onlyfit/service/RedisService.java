package com.shop.onlyfit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Resource(name = "jwtRedisTemplate")
    private final RedisTemplate<String, Object> jwtRedisTemplate;

    public void saveToken(String token, Long userId, Date expirationDate) {
        jwtRedisTemplate.opsForValue().set(token, userId);
        jwtRedisTemplate.expireAt(token, expirationDate);
    }

    public void revokeToken(String token) {
        jwtRedisTemplate.delete(token);
    }

    public Boolean isTokenExpired(String token) {
        return Boolean.FALSE.equals(jwtRedisTemplate.hasKey(token));
    }
}
