package com.shop.onlyfit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {

    private Long id;
    private String placeName;
    private String recipient;
    private String city;
    private String streat;
    private String zipcode;
    private String addressHomePhoneNumber;
    private String addressPhoneNumber;
}
