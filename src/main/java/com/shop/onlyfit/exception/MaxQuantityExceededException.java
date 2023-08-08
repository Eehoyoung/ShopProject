package com.shop.onlyfit.exception;

public class MaxQuantityExceededException extends RuntimeException {
    public MaxQuantityExceededException(String message) {
        super(message);
    }
}
