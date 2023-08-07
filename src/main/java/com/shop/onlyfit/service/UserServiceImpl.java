package com.shop.onlyfit.service;

import com.shop.onlyfit.auth.jwt.JwtTokenUtil;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.LoginType;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.user.UserInfoDto;
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
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtTokenUtil = jwtTokenUtil;
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
        User checkUser = userRepository.findbyusernameorloginid(username, null).orElseGet(() -> {
            return new User();
        });

        return checkUser;
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

    private String changePhoneNumFormat(String phoneNum) {
        if (phoneNum.length() != 11) {
            return phoneNum;
        }
        String newPhoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.subSequence(3, 7) + "-"
                + phoneNum.subSequence(7, 11);
        return newPhoneNum;
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
