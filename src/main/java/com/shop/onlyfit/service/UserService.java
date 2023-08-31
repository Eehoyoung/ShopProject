package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.SearchUser;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MarketInfoDto;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.ProfileDto;
import com.shop.onlyfit.dto.user.UserInfoDto;
import com.shop.onlyfit.dto.user.UserPageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {

    @Transactional
    void changePassword(Long id, String password);

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

    int getVisitCount();

    UserPageDto findAllUserByPaging(Pageable pageable);

    UserPageDto findAllUserByConditionByPaging(SearchUser searchUser, Pageable pageable);

    Object findUserById(Long id);


    void deleteById(Long id);

    User getUserById(Long senderId);

    String findLoginIdByRoomId(String roomId);

    String getUserRole(String name);
}
