package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.Order;
import com.shop.onlyfit.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom{

}
