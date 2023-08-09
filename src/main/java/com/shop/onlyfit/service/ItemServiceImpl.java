package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> MainCarouselItemList() {
        List<Item> mainCarouselList = new ArrayList<>();

        Item firstItem = itemRepository.findByItemIdxAndColorAndRep(94L, "블루", true);
        Item secondItem = itemRepository.findByItemIdxAndColorAndRep(95L, "블랙", true);
        Item thirdItem = itemRepository.findByItemIdxAndColorAndRep(96L, "네이비", true);
        Item fourthItem = itemRepository.findByItemIdxAndColorAndRep(97L, "블랙 M size", true);
        Item fifthItem = itemRepository.findByItemIdxAndColorAndRep(98L, "아이보리", true);

        mainCarouselList.add(firstItem);
        mainCarouselList.add(secondItem);
        mainCarouselList.add(thirdItem);
        mainCarouselList.add(fourthItem);
        mainCarouselList.add(fifthItem);

        return mainCarouselList;
    }

    @Override
    public List<WeeklyBestDto> OuterWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("outer", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> SleeveTopWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("top", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> ShirtsWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("shirts", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> BottomWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("bottom", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> ShoesWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("shoes", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> TopKnitWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("top", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> NewArrivalItem() {
        List<WeeklyBestDto> newArrivalList = itemRepository.findNewArrivalItem("outer", "jacket", true);
        for (WeeklyBestDto weeklyBestDto : newArrivalList) {
            weeklyBestDto.setMileage(weeklyBestDto.getPrice() / 100);
        }

        return newArrivalList;
    }

    @Override
    public List<ItemDto> getAllItemInCart(List<Cart> cartList) {
        List<ItemDto> allItemInCart = new ArrayList<>();

        for (Cart cart : cartList) {
            Long itemId = cart.getItem().getId();
            allItemInCart.add(itemRepository.findAllItemInCart(itemId));
        }
        return allItemInCart;
    }

    @Override
    public ItemPageDto getItemPagingDtoByCategory(Pageable pageable, String firstCategory, String secondCategory) {
        ItemPageDto itemPageDto = new ItemPageDto();

        Page<ItemDto> itemBoards = itemRepository.findAllItem(pageable, firstCategory, secondCategory);
        int homeStartPage = 1;
        int homeEndPage = 2;

        itemPageDto.setItemPage(itemBoards);
        itemPageDto.setHomeStartPage(homeStartPage);
        itemPageDto.setHomeEndPage(homeEndPage);

        return itemPageDto;
    }
}
