package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDto;

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
}
