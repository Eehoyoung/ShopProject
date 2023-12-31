package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<ItemDto> searchAllItemByConditionMaket(String loginId, SearchItem searchItem, Pageable pageable);

    Page<ItemDto> findAllItem(Pageable pageable, String firstCategory, String secondCategory);

    Page<ItemDto> findAllItemByMarketId(Pageable pageable, Long marketId);

    Long searchMaxItemIdx();

    ItemDto findAllItemInCart(Long itemId);

    List<WeeklyBestDto> findWeeklyBestItem(String firstCategory, String secondCategory, boolean rep);

    List<WeeklyBestDto> findNewArrivalItem(String firstCategory, String secondCategory, boolean rep);

    Page<ItemDto> searchAllItemByloginId(String userId, Pageable pageable);

    Page<ItemDto> searchAllItemByMarketId(Long marketId, Pageable pageable);

    Long getLatestItemIdx();

    Page<ItemDto> searchAllItem(Pageable pageable);

    Page<ItemDto> searchAllItemByCondition(SearchItem searchItem, Pageable pageable);
}
