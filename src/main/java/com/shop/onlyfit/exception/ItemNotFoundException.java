package com.shop.onlyfit.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message){
        super(message);
    }
}
