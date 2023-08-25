package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MarketInfoDto;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.ProfileDto;
import com.shop.onlyfit.dto.user.UserInfoDto;

import java.util.Optional;

public interface UserService {

    //회원가입
    Long joinUser(UserInfoDto userInfoDto);

    boolean checkId(String loginId);

    User findByLoginId(String loginId);

    Optional<User> loadUserByLoginId(String loginId);

    MyPageDto showMySimpleInfo(String loginId);

    ProfileDto showProfileData(String loginId);

    void updateProfile(String name, ProfileDto profileDto);

    void deleteUserByLoginId(String loginId);

    User findUserByLoginId(String loginId);

    void joinSeller(String loginId, MarketInfoDto marketInfoDto);

    void changeUserGradeToSeller(Long id);

    String findLoginId(String name, String phoneNum);

    boolean resetPassword(String userId, String name, String phoneNum, String newPassword);

    Long getUserId(String loginId);
}
