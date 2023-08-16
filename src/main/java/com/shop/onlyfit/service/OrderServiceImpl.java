package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.*;
import com.shop.onlyfit.domain.type.DeliveryStatus;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.dto.*;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.ItemRepository;
import com.shop.onlyfit.repository.MileageRepository;
import com.shop.onlyfit.repository.OrderRepository;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MileageRepository mileageRepository;

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

    @Transactional
    @Override
    public void doOrder(Long userId, List<Long> itemList, List<Integer> itemCountList,
                        PaymentAddressDto paymentAddressDto, PaymentPriceDto paymentPriceDto, String payType) {
        Optional<User> findUser = userRepository.findById(userId);

        User checkedFindUser = new User();
        if (findUser.isPresent()) {
            checkedFindUser = findUser.get();
        }

        Delivery delivery = new Delivery();

        UserAddress userAddress = new UserAddress();
        userAddress.setCity(paymentAddressDto.getCity());
        userAddress.setStreet(paymentAddressDto.getStreet());
        userAddress.setZipcode(paymentAddressDto.getZipcode());
        delivery.setUserAddress(userAddress);
        delivery.setDeliveryStatus(DeliveryStatus.READY);

        List<OrderItem> checkedTestOrderItem = new ArrayList<>();

        for (int i = 0; i < itemList.size(); i++) {

            Item checkedItem = itemRepository.findById(itemList.get(i)).orElseThrow(
                    () -> new RuntimeException("해당 상품이 없습니다.")
            );

            OrderItem testOrderItem = OrderItem.createOrderItem(checkedItem, checkedItem.getPrice(), itemCountList.get(i));
            if (payType.equals("card")) {
                testOrderItem.setOrderStatus(OrderStatus.PAYCOMPLETE);
            } else {
                testOrderItem.setOrderStatus(OrderStatus.PAYWAITING);
            }
            checkedTestOrderItem.add(testOrderItem);
        }

        Order order = Order.createOrder(checkedFindUser, delivery, checkedTestOrderItem, payType);
        order.setUsedMileagePrice(paymentPriceDto.getUsed_mileage());

        Mileage mileage = new Mileage();
        mileage.setMileageContent("구매 적립금");
        mileage.setMileagePrice(paymentPriceDto.getTobepaid_price() / 100);
        mileage.setUser(checkedFindUser);

        mileageRepository.save(mileage);
        orderRepository.save(order);
    }// 주문 생성
}
