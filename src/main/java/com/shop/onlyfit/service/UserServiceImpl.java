package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.UserAddress;
import com.shop.onlyfit.domain.type.LoginType;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.ProfileDto;
import com.shop.onlyfit.dto.user.UserInfoDto;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public Long joinUser(UserInfoDto userInfoDto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));

        return userRepository.save(userInfoDto.toEntity()).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkId(String loginId) {
        Optional<User> findUser = userRepository.findbyusernameorloginid(null, loginId);
        return findUser.isPresent();
    }

    @Override
    @Transactional
    public User checkUsername(String username) {

        return userRepository.findbyusernameorloginid(username, null).orElseGet(User::new);
    }

    @Override
    @Transactional
    public int saveUser(User user) {
        try {
            String rawPassword = user.getPassword();
            String encPassword = encoder.encode(rawPassword);
            user.setPhoneNumber(changePhoneNumFormat(user.getPhoneNumber()));
            user.setPassword(encPassword);
            if (!user.getName().startsWith("kakao_")) {
                user.setLoginType(LoginType.ORIGIN);
            }
            user.setUserGrade(UserGrade.USER);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public User findByUserId(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("해당 유저를 찾을 수 없습니다.")
        );
    }

    @Override
    public User findByLoginId(String loginId) {
        return userRepository.findbyusernameorloginid(loginId, loginId).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.")
        );
    }

    @Override
    @Transactional
    public Optional<User> loadUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    @Override
    public MyPageDto showMySimpleInfo(String loginId) {
        int totalMileage = 0;
        int usedMileage = 0;
        MyPageDto myPageDto = new MyPageDto();

        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다")
        );

        for (int i = 0; i < findUser.getMileageList().size(); i++) {
            totalMileage += findUser.getMileageList().get(i).getMileagePrice();
        }
        for (int j = 0; j < findUser.getOrderList().size(); j++) {
            usedMileage += findUser.getOrderList().get(j).getUsedMileagePrice();
        }

        myPageDto.setName(findUser.getName());
        myPageDto.setGrade(findUser.getUserGrade());
        myPageDto.setMileage(totalMileage - usedMileage);

        return myPageDto;
    }

    @Override
    public ProfileDto showProfileData(String loginId) {
        ProfileDto myProfileDto = new ProfileDto();

        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원을 찾을 수 없습니다")
        );

        String homePhoneNumber = findUser.getHomePhoneNumber();

        if(homePhoneNumber == null){
            homePhoneNumber = "000,0000,0000";
        }

        String[] homePhoneNumberArr = homePhoneNumber.split(",");
        String phoneNumber = findUser.getPhoneNumber();
        String[] phoneNumberArr = phoneNumber.split(",");
        String birthday = findUser.getBirthday();
        String[] birthdayArr = birthday.split(",");

        if (findUser.getUserAddress() == null) {
            findUser.setUserAddress(new UserAddress("", "", ""));
        }

        myProfileDto.setName(findUser.getName());
        myProfileDto.setLoginId(findUser.getLoginId());
        myProfileDto.setStreet(findUser.getUserAddress().getStreet());
        myProfileDto.setZipcode(findUser.getUserAddress().getZipcode());
        myProfileDto.setCity(findUser.getUserAddress().getCity());
        myProfileDto.setHomePhoneNumber(homePhoneNumberArr);
        myProfileDto.setPhoneNumber(phoneNumberArr);
        myProfileDto.setEmail(findUser.getEmail());
        myProfileDto.setBirthday(birthdayArr);

        return myProfileDto;
    }


    @Transactional
    @Override
    public void updateProfile(String loginId, ProfileDto profileDto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원을 찾을 수 없습니다")
        );

        String homePhoneNumberResult = profileDto.getHomePhoneNumber()[0] + "," + profileDto.getHomePhoneNumber()[1] + "," + profileDto.getHomePhoneNumber()[2];
        String phoneNumberResult = profileDto.getPhoneNumber()[0] + "," + profileDto.getPhoneNumber()[1] + "," + profileDto.getPhoneNumber()[2];
        UserAddress memberAddress = new UserAddress(profileDto.getCity(), profileDto.getStreet(), profileDto.getZipcode());

        findUser.setName(profileDto.getName());
        findUser.setLoginId(profileDto.getLoginId());
        findUser.setPassword(passwordEncoder.encode(profileDto.getPassword()));

        findUser.setHomePhoneNumber(homePhoneNumberResult);
        findUser.setPhoneNumber(phoneNumberResult);
        findUser.setEmail(profileDto.getEmail());
        findUser.setUserAddress(memberAddress);
    }

    @Override
    @Transactional
    public void deleteUserByLoginId(String loginId) {
        userRepository.deleteByLoginId(loginId);
    }

    private String changePhoneNumFormat(String phoneNum) {
        if (phoneNum.length() != 11) {
            return phoneNum;
        }
        return phoneNum.substring(0, 3) + "-" + phoneNum.subSequence(3, 7) + "-"
                + phoneNum.subSequence(7, 11);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User userEntity = userRepository.findbyusernameorloginid(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if ("admin@example.com".equals(userEntity.getLoginId())) {
            authorities.add(new SimpleGrantedAuthority(UserGrade.ADMIN.getValue()));
            userEntity.setUserGrade(UserGrade.ADMIN);
        } else {
            authorities.add(new SimpleGrantedAuthority(UserGrade.USER.getValue()));
            userEntity.setUserGrade(UserGrade.USER);
        }

        int visitCount = userEntity.getVisitCount();
        userEntity.setVisitCount(++visitCount);

        return new org.springframework.security.core.userdetails.User(userEntity.getLoginId(), userEntity.getPassword(), authorities);
    }
}
