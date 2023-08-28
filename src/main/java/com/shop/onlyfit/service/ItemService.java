package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDetailDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemListToOrderDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    List<Item> MainCarouselItemList();
//    Main Carousel에 상품을 담아 반환하는 메소드

    List<WeeklyBestDto> OuterWeeklyBestItem();

    //    Outer 위클리 베스트 상품 반환
    List<WeeklyBestDto> SleeveTopWeeklyBestItem();

    List<WeeklyBestDto> ShirtsWeeklyBestItem();

    List<WeeklyBestDto> BottomWeeklyBestItem();

    List<WeeklyBestDto> ShoesWeeklyBestItem();

    List<WeeklyBestDto> TopKnitWeeklyBestItem();

    List<WeeklyBestDto> NewArrivalItem();

    List<ItemDto> getAllItemInCart(List<Cart> cartList);

    ItemPageDto getItemPagingDtoByCategory(Pageable pageable, String firstCategory, String secondCategory);

    ItemPageDto getItemPagingDtoByCategoryAndMarket(Pageable pageable, Long marketId);

    ItemDetailDto getItemDetailDto(Long itemIdx);

    void moveItemToCart(String name, Long itemIdx, String color, int quantity);

    List<ItemDto> itemToPayment(String itemList);

    ItemListToOrderDto itemToOrder(String orderItemInfo);

    void changeItemStatusSoldOut(String itemIdx, String itemColor);

    Page<ItemDto> findAllItem(Pageable pageable);

    ItemPageDto findAllItemByPaging(Pageable pageable);

    ItemPageDto findAllItemByConditionByPaging(SearchItem searchItem, Pageable pageable);

    void changeItemStatusOnSale(String itemIdx, String itemColor);

    void deleteItemById(String itemIdx, String itemColor);
}
