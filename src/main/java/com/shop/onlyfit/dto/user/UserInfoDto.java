package com.shop.onlyfit.dto.user;

import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.LoginType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//회원가입 Dto
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private String birthday;
    private LoginType loginType;


    public User toEntity() {
        return User.builder()
                .id(id)
                .loginId(loginId)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .birthday(birthday)
                .loginType(loginType)
                .build();
    }

}