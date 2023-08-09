package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Order;
import com.shop.onlyfit.domain.OrderItem;
import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.dto.MainPageOrderDto;
import com.shop.onlyfit.dto.MyPageOrderStatusDto;
import com.shop.onlyfit.dto.OrderMainPageDto;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.OrderRepository;
import com.shop.onlyfit.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public MyPageOrderStatusDto showOrderStatus(String loginId) {
        int payWaitingNum = 0;
        int preShipNum = 0;
        int inShipNum = 0;
        int completeShip = 0;
        int orderCancelNum = 0;
        int orderChangeNum = 0;
        int orderRefundNum = 0;

        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당하는 회원을 찾을 수 없습니다")
        );

        for (int i = 0; i < user.getOrderList().size(); i++) {
            Order order = user.getOrderList().get(i);
            for (int j = 0; j < order.getOrderItemList().size(); j++) {
                OrderItem orderItem = order.getOrderItemList().get(j);
                if (orderItem.getOrderStatus().equals(OrderStatus.PAYWAITING)) payWaitingNum += 1;
                if (orderItem.getOrderStatus().equals(OrderStatus.INSHIP)) inShipNum += 1;
                if (orderItem.getOrderStatus().equals(OrderStatus.PRESHIP)) preShipNum += 1;
                if (orderItem.getOrderStatus().equals(OrderStatus.COMPLETESHIP)) completeShip += 1;
                if (orderItem.getOrderStatus().equals(OrderStatus.ORDERCANCEL)) orderCancelNum += 1;
                if (orderItem.getOrderStatus().equals(OrderStatus.ORDERCHANGE)) orderChangeNum += 1;
                if (orderItem.getOrderStatus().equals(OrderStatus.ORDERREFUND)) orderRefundNum += 1;
            }
        }

        return new MyPageOrderStatusDto(payWaitingNum, preShipNum, inShipNum, completeShip, orderCancelNum, orderChangeNum, orderRefundNum);
    }

    @Override
    public OrderMainPageDto getOrderPagingDto(SearchOrder searchOrder, Pageable pageable, String loginId) {
        OrderMainPageDto orderPageDto = new OrderMainPageDto();

        Page<MainPageOrderDto> mainPageOrderBoards;

        if (StringUtils.isEmpty(searchOrder.getFirstdate()) && StringUtils.isEmpty(searchOrder.getLastdate())) {
            mainPageOrderBoards = orderRepository.mainPageSearchAllOrder(pageable, loginId);
        } else {
            mainPageOrderBoards = orderRepository.mainPageSearchAllOrderByCondition(searchOrder, pageable, loginId);
        }
        int homeStartPage = Math.max(1, mainPageOrderBoards.getPageable().getPageNumber() - 4);
        int homeEndPage = Math.min(mainPageOrderBoards.getTotalPages(), mainPageOrderBoards.getPageable().getPageNumber() + 4);

        orderPageDto.setMainPageOrderBoards(mainPageOrderBoards);
        orderPageDto.setHomeStartPage(homeStartPage);
        orderPageDto.setHomeEndPage(homeEndPage);

        return orderPageDto;
    }
}
