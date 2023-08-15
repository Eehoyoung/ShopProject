package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.domain.SearchOrder;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.domain.type.PostCompany;
import com.shop.onlyfit.dto.OrderDto;
import com.shop.onlyfit.dto.OrderPageDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketService {
    Page<User> findAllMemberByOrderByCreatedAt(Pageable pageable);

    Page<ItemDto> findAllItem(Pageable pageable);

    Page<OrderDto> findAllOrder(Pageable pageable);

    int getVisitCount();

    Long getMaxItemIdx();

    void saveItem(Item item);

    ItemPageDto findAllItemByPaging(String userId, Pageable pageable);

    ItemPageDto findAllItemByConditionByPaging(String userId, SearchItem searchItem, Pageable pageable);

    String findMarketNameByMarketId(Long marketId);

    void updateMarketVisitCount(Long marketId);

    Long findMarketId(String userId);

    Page<ItemDto> findAllItemByMarketId(Long marketId, Pageable pageable);

    Page<OrderDto> findAllOrderByMarketId(Long marketId, Pageable pageable);


    int getVisitCountByMarketId(Long marketId);

    OrderPageDto findAllOrderByPaging(Long marketId, Pageable pageable);

    OrderPageDto findAllOrderByConditionByPaging(Long marketId, SearchOrder searchOrder, Pageable pageable);

    Long changeOrderStatus(Long id, OrderStatus status, PostCompany postCompany, String postNumber);
}
