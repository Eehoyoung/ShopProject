package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.OrderItem;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.exception.ItemNotFoundException;
import com.shop.onlyfit.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public void changeOrderStatus(Long id, OrderStatus status) {
        OrderItem findOrderItem = orderItemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("해당하는 상품이 존재하지 않습니다")
        );
        findOrderItem.setOrderStatus(status);
    }
}
