package com.shop.onlyfit.dto;

import com.shop.onlyfit.domain.type.UserGrade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageDto {
    private String name;
    private UserGrade grade;
    private int mileage;
}
