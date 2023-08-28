package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<User> findAllUserByOrderByCreateAt(Pageable pageable);


}
