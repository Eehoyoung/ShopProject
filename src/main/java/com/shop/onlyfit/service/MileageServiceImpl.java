package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Mileage;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.MileagePageDto;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.MileageRepository;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements MileageService {

    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;
    @Override
    @Transactional
    public Long joinMileage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new LoginIdNotFoundException("사용자를 찾을 수 없습니다.")
        );
        Mileage mileage = new Mileage();
        mileage.setMileagePrice(2000);
        mileage.setMileageContent("회원가입을 축하합니다.");
        mileage.setUser(user);

        mileageRepository.save(mileage);

        return mileage.getId();
    }

    @Override
    public int getTotalMileage(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("사용자를 찾을 수 없습니다.")
        );
        int totalMileage = 0;

        for (int i = 0; i < user.getMileageList().size(); i++) {
            totalMileage += user.getMileageList().get(i).getMileagePrice();
        }
        return totalMileage;
    }

    @Override
    public int getTotalUsedMileage(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("사용자를 찾을 수 없습니다.")
        );
        int totalUsedMileage = 0;

        for (int i = 0; i < user.getOrderList().size(); i++) {
            totalUsedMileage += user.getOrderList().get(i).getUsedMileagePrice();
        }
        return totalUsedMileage;
    }

    @Override
    public MileagePageDto getMileagePagingDto(String loginId, Pageable pageable) {

        MileagePageDto mileagePageDto = new MileagePageDto();

        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원이 존재하지 않습니다")
        );
        Page<Mileage> mileageBoards = mileageRepository.findAllByUser(findUser, pageable);
        int homeStartPage = Math.max(1, mileageBoards.getPageable().getPageNumber() - 4);
        int homeEndPage = Math.min(mileageBoards.getTotalPages(), mileageBoards.getPageable().getPageNumber() + 4);

        mileagePageDto.setMileageBoards(mileageBoards);
        mileagePageDto.setHomeStartPage(homeStartPage);
        mileagePageDto.setHomeEndPage(homeEndPage);

        return mileagePageDto;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void birthMileage() {
        LocalDate now = LocalDate.now();
        List<User> userList = userRepository.findByBirthday(now);
        for (User user : userList) {
            Mileage mileage = new Mileage();
            mileage.setMileageContent("생일을 축하 합니다.");
            mileage.setMileagePrice(5000);
            mileage.setUser(user);
            mileageRepository.save(mileage);
        }
    }
}
