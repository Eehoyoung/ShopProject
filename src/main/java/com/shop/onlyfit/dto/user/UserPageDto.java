package com.shop.onlyfit.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class UserPageDto {
    Page<UserDto> userPage;
    int StartPage;
    int EndPage;
}
