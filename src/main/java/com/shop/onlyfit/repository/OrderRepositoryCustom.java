package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.dto.MainPageOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<MainPageOrderDto> mainPageSearchAllOrder(Pageable pageable, String loginId);

    Page<MainPageOrderDto> mainPageSearchAllOrderByCondition(SearchOrder searchOrder, Pageable pageable, String loginId);

}
