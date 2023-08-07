package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Mileage;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.repository.MileageRepository;
import com.shop.onlyfit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MileageServiceImpl implements MileageService {

    private final UserRepository userRepository;
    private final MileageRepository mileageRepository;

    @Autowired
    public MileageServiceImpl(UserRepository userRepository, MileageRepository mileageRepository) {
        this.userRepository = userRepository;
        this.mileageRepository = mileageRepository;
    }

    @Override
    @Transactional
    public Long joinMileage(Long userId) {
        User user = userRepository.findById(userId).get();
        Mileage mileage = new Mileage();
        mileage.setMileagePrice(2000);
        mileage.setMileageContent("회원가입을 축하합니다.");
        mileage.setUser(user);

        mileageRepository.save(mileage);

        return mileage.getId();
    }
}
