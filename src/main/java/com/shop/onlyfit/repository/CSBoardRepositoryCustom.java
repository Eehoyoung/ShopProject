package com.shop.onlyfit.repository;

import com.shop.onlyfit.dto.CustomServiceBoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CSBoardRepositoryCustom {
    Page<CustomServiceBoardDto> findByTitleContaining(String title, Pageable pageable);
}
