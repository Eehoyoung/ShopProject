package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public Page<User> findAllUserByOrderByCreateAt(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAt(pageable);
    }
}
