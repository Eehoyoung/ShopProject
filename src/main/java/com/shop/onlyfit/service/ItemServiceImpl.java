package com.shop.onlyfit.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shop.onlyfit.domain.Cart;
import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.WeeklyBestDto;
import com.shop.onlyfit.dto.item.ItemDetailDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemListToOrderDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.exception.LoginIdNotFoundException;
import com.shop.onlyfit.repository.CartRepository;
import com.shop.onlyfit.repository.ItemRepository;
import com.shop.onlyfit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Override
    public List<Item> MainCarouselItemList() {
        List<Item> mainCarouselList = new ArrayList<>();
        Long idx = itemRepository.getLatestItemIdx();
        for (int i = 0; i < 5; i++) {
            String color = itemRepository.findItemColorByItemIdx(idx);
            Item item = itemRepository.findByItemIdxAndColorAndRep(--idx, color, true);
            mainCarouselList.add(item);
        }
        return mainCarouselList;
    }

    @Override
    public List<WeeklyBestDto> OuterWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("outer", "jacket", true);
    }

    @Override
    public List<WeeklyBestDto> SleeveTopWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("top", "longsleeve", true);
    }

    @Override
    public List<WeeklyBestDto> ShirtsWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("shirts", "basic", true);
    }

    @Override
    public List<WeeklyBestDto> BottomWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("bottom", "cotton", true);
    }

    @Override
    public List<WeeklyBestDto> ShoesWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("shoes", "shoes", true);
    }

    @Override
    public List<WeeklyBestDto> TopKnitWeeklyBestItem() {
        return itemRepository.findWeeklyBestItem("top", "knit", true);
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
    public ItemPageDto getItemPagingDtoByCategoryAndMarket(Pageable pageable, Long marketId) {
        ItemPageDto itemPageDto = new ItemPageDto();

        Page<ItemDto> itemBoards = itemRepository.findAllItemByMarketId(pageable, marketId);
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
        Long marketId = itemRepository.findMarketIdByItemIdx(itemIdx);
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
        itemDetailDto.setMarketId(marketId);

        return itemDetailDto;
    }

    @Override
    public void moveItemToCart(String loginId, Long itemIdx, String itemColor, int quantity) {

        Cart cart = new Cart();
        User findUser = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new LoginIdNotFoundException("해당 사용자를 찾을 수 없습니다.")
        );
        Item findItem = itemRepository.findByItemIdxAndColorAndRep(itemIdx, itemColor, true);
        cart.setUser(findUser);
        cart.setCartCount(quantity);
        cart.setItem(findItem);

        cartRepository.save(cart);
    }

    @Override
    public List<ItemDto> itemToPayment(String itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        ItemDto itemDto = new ItemDto();

        JsonArray jsonArray = new Gson().fromJson(itemList, JsonArray.class);
        JsonObject object = (JsonObject) jsonArray.get(0);

        String id = object.get("idx").getAsString();
        String color = object.get("color").getAsString();
        String quantity = object.get("quantity").getAsString();

        Long itemIdx = Long.parseLong(id);
        int orderCount = Integer.parseInt(quantity);

        Item byItemIdxAndColorAndRep = itemRepository.findByItemIdxAndColorAndRep(itemIdx, color, true);
        itemDto.setItemIdx(byItemIdxAndColorAndRep.getItemIdx());
        itemDto.setItemName(byItemIdxAndColorAndRep.getItemName());
        itemDto.setColor(byItemIdxAndColorAndRep.getColor());
        itemDto.setCartConunt(orderCount);
        itemDto.setId(byItemIdxAndColorAndRep.getId());
        itemDto.setPrice(byItemIdxAndColorAndRep.getPrice());
        itemDto.setImgUrl(byItemIdxAndColorAndRep.getImgUrl());

        itemDtoList.add(itemDto);

        return itemDtoList;
    }

    @Override
    public ItemListToOrderDto itemToOrder(String orderItemInfo) {
        ItemListToOrderDto itemListToOrderDto = new ItemListToOrderDto();

        JsonArray jsonArray = new Gson().fromJson(orderItemInfo, JsonArray.class);

        List<Long> itemList = new ArrayList<>();
        List<Integer> itemCountList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject object = (JsonObject) jsonArray.get(i);
            String item_idx = object.get("item_idx").getAsString();
            String item_color = object.get("item_color").getAsString();
            String item_quantity = object.get("item_quantity").getAsString();

            Long itemIdx = Long.parseLong(item_idx);
            int itemOrderCount = Integer.parseInt(item_quantity);

            Item findItem = itemRepository.findByItemIdxAndColorAndRep(itemIdx, item_color, true);

            itemList.add(findItem.getId());
            itemCountList.add(itemOrderCount);
        }

        itemListToOrderDto.setItemList(itemList);
        itemListToOrderDto.setItemCountList(itemCountList);

        return itemListToOrderDto;
    }
}
