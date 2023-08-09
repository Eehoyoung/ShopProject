package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.Item;
import com.shop.onlyfit.domain.SearchItem;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.dto.OrderDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.repository.ItemRepository;
import com.shop.onlyfit.repository.OrderRepository;
import com.shop.onlyfit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MarketServiceImpl implements MarketService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public MarketServiceImpl(UserRepository userRepository, ItemRepository itemRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<User> findAllMemberByOrderByCreatedAt(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAt(pageable);
    }

    @Override
    public Page<ItemDto> findAllItem(Pageable pageable) {
        return itemRepository.searchAllItem(pageable);
    }

    @Override
    public Page<OrderDto> findAllOrder(Pageable pageable) {
        return orderRepository.searchAllOrder(pageable);
    }

    @Override
    public int getVisitCount() {
        return userRepository.visitCountResult();
    }

    @Override
    public Long getMaxItemIdx() {
        return itemRepository.searchMaxItemIdx();
    }

    @Override
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Override
    public ItemPageDto findAllItemByPaging(String userId,Pageable pageable) {

        ItemPageDto itemPageDto = new ItemPageDto();

        Page<ItemDto> itemBoards = itemRepository.searchAllItemByloginId(userId,pageable);

        int homeStartPage = Math.max(1, itemBoards.getPageable().getPageNumber() - 1);
        int homeEndPage = Math.min(itemBoards.getTotalPages(), itemBoards.getPageable().getPageNumber() + 3);

        itemPageDto.setItemPage(itemBoards);
        itemPageDto.setHomeStartPage(homeStartPage);
        itemPageDto.setHomeEndPage(homeEndPage);

        return itemPageDto;
    }

    @Override
    public ItemPageDto findAllItemByConditionByPaging(String userId, SearchItem searchItem, Pageable pageable) {
        ItemPageDto itemPageDto = new ItemPageDto();
        Page<ItemDto> itemBoards = itemRepository.searchAllItemByCondition(userId, searchItem, pageable);

        int homeStartPage = Math.max(1, itemBoards.getPageable().getPageNumber() - 1);
        int homeEndPage = Math.min(itemBoards.getTotalPages(), itemBoards.getPageable().getPageNumber() + 3);

        itemPageDto.setItemPage(itemBoards);
        itemPageDto.setHomeStartPage(homeStartPage);
        itemPageDto.setHomeEndPage(homeEndPage);

        return itemPageDto;
    }
}
