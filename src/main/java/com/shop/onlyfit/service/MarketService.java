package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.OrderDto;
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
}
