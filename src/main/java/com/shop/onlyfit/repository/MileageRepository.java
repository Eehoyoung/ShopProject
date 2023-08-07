package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.Mileage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileageRepository extends JpaRepository<Mileage, Long> {
}
