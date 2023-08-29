package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Market;
import com.shop.onlyfit.domain.SearchUser;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.UserAddress;
import com.shop.onlyfit.domain.type.LoginType;
import com.shop.onlyfit.domain.type.UserGrade;
import com.shop.onlyfit.dto.MarketInfoDto;
import com.shop.onlyfit.dto.MyPageDto;
import com.shop.onlyfit.dto.ProfileDto;
import com.shop.onlyfit.dto.user.UserDto;
import com.shop.onlyfit.dto.user.UserInfoDto;
import com.shop.onlyfit.dto.user.UserPageDto;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.MarketRepository;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final BCryptPasswordEncoder encoder;

    public String getUserName(String loginId) {
        return userRepository.findUserNameByLoginId(loginId);
    }

    public String getUserNameByUserCode(Long userId) {
        return userRepository.findUserNameByUserId(userId);
    }

    @Transactional
    @Override
    public void changePassword(Long id, String password) {
        User user = findByUserId(id);
        user.setPassword(password);
    }

    @Override
    @Transactional
    public Long joinUser(UserInfoDto userInfoDto) {
        userInfoDto.setPassword(encoder.encode(userInfoDto.getPassword()));
        userInfoDto.setLoginType(LoginType.ORIGIN);
        return userRepository.save(userInfoDto.toEntity()).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkId(String loginId) {
        Optional<User> findUser = userRepository.findByUsernameOrLoginId(null, loginId);
        return findUser.isPresent();
    }

    @Transactional(readOnly = true)
    public boolean checkName(String name) {
        Optional<Market> findMarket = marketRepository.findByName(name);
        return findMarket.isPresent();
    }


    public User findByUserId(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("해당 유저를 찾을 수 없습니다.")
        );
    }

    @Override
    public User findByLoginId(String loginId) {
        return userRepository.findByUsernameOrLoginId(loginId, loginId).orElseThrow(
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
                () -> new LoginIdNotFoundException("해당 유저를 찾을 수 없습니다.")
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
                () -> new LoginIdNotFoundException("해당 유저를 찾을 수 없습니다.")
        );

        String homePhoneNumber = findUser.getHomePhoneNumber();
        String phoneNumber = findUser.getPhoneNumber();
        String birthday = findUser.getBirthday();

        if (homePhoneNumber == null) {
            homePhoneNumber = "000,0000,0000";
        }
        if (phoneNumber == null) {
            phoneNumber = "000,0000,0000";
        }
        if (birthday == null) {
            birthday = "0000,00,00";
        }

        String[] homePhoneNumberArr = homePhoneNumber.split(",");

        String[] phoneNumberArr = phoneNumber.split(",");

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

        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당 유저를 찾을 수 없습니다.")
        );

        String homePhoneNumberResult = profileDto.getHomePhoneNumber()[0] + "," + profileDto.getHomePhoneNumber()[1] + "," + profileDto.getHomePhoneNumber()[2];
        String phoneNumberResult = profileDto.getPhoneNumber()[0] + "," + profileDto.getPhoneNumber()[1] + "," + profileDto.getPhoneNumber()[2];
        UserAddress userAddress = new UserAddress(profileDto.getCity(), profileDto.getStreet(), profileDto.getZipcode());

        findUser.setName(profileDto.getName());
        findUser.setLoginId(profileDto.getLoginId());
        findUser.setPassword(encoder.encode(profileDto.getPassword()));

        findUser.setHomePhoneNumber(homePhoneNumberResult);
        findUser.setPhoneNumber(phoneNumberResult);
        findUser.setEmail(profileDto.getEmail());
        findUser.setUserAddress(userAddress);
    }

    @Override
    @Transactional
    public void deleteUserByLoginId(String loginId) {
        userRepository.deleteByLoginId(loginId);
    }

    @Override
    public User findUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당 하는 회원이 없습니다.")
        );
    }

    @Override
    @Transactional
    public void joinSeller(String loginId, MarketInfoDto marketInfoDto) {
        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당 유저를 찾을 수 없습니다.")
        );
        marketInfoDto.setUser(findUser);
        marketInfoDto.setVisitCount(0);
        marketRepository.save(marketInfoDto.toEntity());
    }

    @Override
    @Transactional
    public void changeUserGradeToSeller(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.setUserGrade(UserGrade.SELLER);
        userRepository.save(user);
    }

    @Override
    public String findLoginId(String name, String phoneNum) {
        return userRepository.findByFindLoginId(name, changePhoneNumFormat(phoneNum));
    }

    @Override
    public boolean resetPassword(String userId, String name, String phoneNum, String newPassword) {
        Long flag = userRepository.checkUserInfo(userId, name, phoneNum);
        if (flag > 0) {
            User user = new User();
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
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
        User userEntity = userRepository.findByUsernameOrLoginId(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if ("admin@example.com".equals(userEntity.getLoginId())) {
            authorities.add(new SimpleGrantedAuthority(UserGrade.ADMIN.getValue()));
            userEntity.setUserGrade(UserGrade.ADMIN);
        } else if (userEntity.getUserGrade() == UserGrade.SELLER) {
            authorities.add(new SimpleGrantedAuthority(UserGrade.SELLER.getValue()));
            userEntity.setUserGrade(UserGrade.SELLER);
        } else {
            authorities.add(new SimpleGrantedAuthority(UserGrade.USER.getValue()));
            userEntity.setUserGrade(UserGrade.USER);
        }

        int visitCount = userEntity.getVisitCount();
        userEntity.setVisitCount(++visitCount);

        return new org.springframework.security.core.userdetails.User(userEntity.getLoginId(), userEntity.getPassword(), authorities);
    }

    @Override
    public Long getUserId(String loginId) {
        return userRepository.findUserId(loginId);
    }

    @Override
    public int getVisitCount() {
        return userRepository.visitCountResult();
    }

    @Override
    public UserPageDto findAllUserByPaging(Pageable pageable) {
        UserPageDto userPageDto = new UserPageDto();
        Page<UserDto> userBoards = userRepository.searchAll(pageable);
        int homeStartPage = Math.max(1, userBoards.getPageable().getPageNumber());
        int homeEndPage = Math.min(userBoards.getTotalPages(), userBoards.getPageable().getPageNumber() + 5);

        userPageDto.setUserPage(userBoards);
        userPageDto.setStartPage(homeStartPage);
        userPageDto.setEndPage(homeEndPage);

        return userPageDto;
    }

    @Override
    public UserPageDto findAllUserByConditionByPaging(SearchUser searchUser, Pageable pageable) {
        UserPageDto userPageDto = new UserPageDto();
        Page<UserDto> memberBoards = userRepository.searchByCondition(searchUser, pageable);

        int startPage = Math.max(1, memberBoards.getPageable().getPageNumber() - 2);
        int endPage = Math.min(memberBoards.getTotalPages(), startPage + 4);

        userPageDto.setUserPage(memberBoards);
        userPageDto.setStartPage(startPage);
        userPageDto.setEndPage(endPage);

        return userPageDto;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다")
        );
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long senderId) {
        return userRepository.findById(senderId).orElseThrow(
                () -> new RuntimeException("회원을 찾을 수 없습니다.")
        );
    }

    @Override
    public String findLoginIdByRoomId(String roomId) {
        return userRepository.findLoginIdByRoom(Long.parseLong(roomId));
    }
}
