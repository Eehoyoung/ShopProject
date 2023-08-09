package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.Mileage;
import com.shop.onlyfit.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileageRepository extends JpaRepository<Mileage, Long> {
    Page<Mileage> findAllByUser(User findUser, Pageable pageable);
}
