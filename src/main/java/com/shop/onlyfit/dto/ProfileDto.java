package com.shop.onlyfit.dto;

import com.shop.onlyfit.domain.type.UserGrade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private UserGrade userGrade;
    private String city;
    private String street;
    private String zipcode;
    private String[] homePhoneNumber;
    private String[] phoneNumber;
    private String[] birthday;
    private String email;
}
