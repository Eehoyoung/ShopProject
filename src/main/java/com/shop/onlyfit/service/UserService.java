package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.ProfileDto;
import com.shop.onlyfit.dto.user.UserInfoDto;

import java.util.Optional;

public interface UserService {

    //회원가입
    Long joinUser(UserInfoDto userInfoDto);

    boolean checkId(String loginId);

    User checkUsername(String username);

    int saveUser(User user);

    User findByLoginId(String loginId);

    Optional<User> loadUserByLoginId(String loginId);

    MyPageDto showMySimpleInfo(String loginId);

    ProfileDto showProfileData(String loginId);

    void updateProfile(String name, ProfileDto profileDto);

    void deleteUserByLoginId(String loginId);

    User findUserByLoginId(String loginId);
}
