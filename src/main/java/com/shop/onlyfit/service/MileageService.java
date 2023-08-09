package com.shop.onlyfit.service;

import com.shop.onlyfit.dto.MileagePageDto;
import org.springframework.data.domain.Pageable;

public interface MileageService {

    Long joinMileage(Long userId);

    int getTotalMileage(String loginId);

    int getTotalUsedMileage(String loginId);

    MileagePageDto getMileagePagingDto(String loginId, Pageable pageable);
}
