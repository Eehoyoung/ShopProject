package com.shop.onlyfit.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String s) {
        super(s);
    }
}
