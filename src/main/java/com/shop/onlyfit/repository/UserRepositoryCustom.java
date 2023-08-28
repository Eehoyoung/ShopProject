package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.SearchUser;
import com.shop.onlyfit.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<UserDto> searchAll(Pageable pageable);

    Page<UserDto> searchByCondition(SearchUser search, Pageable pageable);
}
