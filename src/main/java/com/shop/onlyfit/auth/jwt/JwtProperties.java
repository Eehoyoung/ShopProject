package com.shop.onlyfit.auth.jwt;

public interface JwtProperties {
    String SECRET = "f9196d081ec5875002d53352ab26f8d4932fe6845a9456e80a8396faabd597de3fa10d469aa25ac477831bc8b90e4e9f82a9dfc89d5c63b34aad359f7016ee9f";
    int EXPIRATION_TIME = 864000000; //60000 1분 //864000000 10일
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
