package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDetailDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.repository.CartRepository;
import com.shop.onlyfit.repository.ItemRepository;
import com.shop.onlyfit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
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

    @Override
    public ItemDetailDto getItemDetailDto(Long itemIdx) {

        List<Item> itemListByItemIdx = itemRepository.findAllByItemIdx(itemIdx);

        List<Item> findItemByitemIdxAndRep = itemRepository.findAllByItemIdxAndRep(itemIdx, true);
        String imgMainUrl = findItemByitemIdxAndRep.get(0).getImgUrl();
        List<String> getColorList = new ArrayList<>();
        for (Item item : findItemByitemIdxAndRep) {
            getColorList.add(item.getColor());
        }

        Item topItemByItemIdxAndRep = itemRepository.findTopByItemIdxAndRep(itemIdx, true);
        String itemName = topItemByItemIdxAndRep.getItemName();
        int price = topItemByItemIdxAndRep.getPrice();
        String itemInfo = topItemByItemIdxAndRep.getItemInfo();
        String itemFabric = topItemByItemIdxAndRep.getFavric();
        String itemModel = topItemByItemIdxAndRep.getModel();
        double mileage = topItemByItemIdxAndRep.getPrice() * 0.01;

        List<Long> idList = new ArrayList<>();
        for (Item listByItemIdx : itemListByItemIdx) {
            idList.add(listByItemIdx.getId());
        }

        List<String> imgUrlList = new ArrayList<>();
        List<Item> itemByItemIdxAndColor = itemRepository.findAllByItemIdxAndColor(itemIdx, topItemByItemIdxAndRep.getColor());

        for (Item item : itemByItemIdxAndColor) {
            imgUrlList.add(item.getImgUrl());
        }

        ItemDetailDto itemDetailDto = new ItemDetailDto();
        itemDetailDto.setImgMainUrl(imgMainUrl);
        itemDetailDto.setColorList(getColorList);
        itemDetailDto.setItemName(itemName);
        itemDetailDto.setPrice(price);
        itemDetailDto.setItemInfo(itemInfo);
        itemDetailDto.setFabric(itemFabric);
        itemDetailDto.setModel(itemModel);
        itemDetailDto.setItemIdx(itemIdx);
        itemDetailDto.setItemId(idList);
        itemDetailDto.setMileage(mileage);
        itemDetailDto.setImgUrlList(imgUrlList);

        return itemDetailDto;
    }

    @Override
    public void moveItemToBasket(String loginId, Long itemIdx, String itemColor, int quantity) {

        Cart cart = new Cart();
        User findUser = userRepository.findByLoginId(loginId).get();
        Item findItem = itemRepository.findByItemIdxAndColorAndRep(itemIdx, itemColor, true);

        cart.setUser(findUser);
        cart.setCartCount(quantity);
        cart.setItem(findItem);

        cartRepository.save(cart);
    }
}
