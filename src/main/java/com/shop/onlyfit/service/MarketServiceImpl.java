package com.shop.onlyfit.service;

import com.shop.onlyfit.domain.*;
import com.shop.onlyfit.domain.type.OrderStatus;
import com.shop.onlyfit.domain.type.PostCompany;
import com.shop.onlyfit.dto.OrderDto;
import com.shop.onlyfit.dto.OrderPageDto;
import com.shop.onlyfit.dto.item.ItemDto;
import com.shop.onlyfit.dto.item.ItemPageDto;
import com.shop.onlyfit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MarketServiceImpl implements MarketService {

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final MarketRepository marketRepository;

    @Autowired
    public MarketServiceImpl(UserRepository userRepository, OrderItemRepository orderItemRepository, ItemRepository itemRepository, OrderRepository orderRepository, MarketRepository marketRepository) {
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.marketRepository = marketRepository;
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
    public ItemPageDto findAllItemByPaging(String userId, Pageable pageable) {

        ItemPageDto itemPageDto = new ItemPageDto();

        Page<ItemDto> itemBoards = itemRepository.searchAllItemByloginId(userId, pageable);

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

    @Override
    public String findMarketNameByMarketId(Long marketId) {
        return marketRepository.findMarketNameByMarketId(marketId);
    }

    @Override
    @Transactional
    public void updateMarketVisitCount(Long marketId) {
        Market market = marketRepository.findById(marketId).get();
        int visitCount = marketRepository.findVisitCountByMarketId(marketId);
        market.setVisitCount(++visitCount);
        marketRepository.save(market);
    }

    @Override
    public Long findMarketId(String userId) {
        return marketRepository.findMarketIdByLoginId(userId);
    }

    @Override
    public Page<ItemDto> findAllItemByMarketId(Long marketId, Pageable pageable) {
        return itemRepository.searchAllItemByMarketId(marketId, pageable);
    }

    @Override
    public Page<OrderDto> findAllOrderByMarketId(Long marketId, Pageable pageable) {
        return orderRepository.searchAllOrderByMarketId(marketId, pageable);
    }

    @Override
    public int getVisitCountByMarketId(Long marketId) {
        return userRepository.visitCountResultByMarketId(marketId);
    }

    @Override
    public OrderPageDto findAllOrderByPaging(Long marketId, Pageable pageable) {

        OrderPageDto orderPageDto = new OrderPageDto();

        Page<OrderDto> orderBoards = orderRepository.searchAllOrderByMarketId(marketId, pageable);
        int homeStartPage = Math.max(1, orderBoards.getPageable().getPageNumber() - 4);
        int homeEndPage = Math.min(orderBoards.getTotalPages(), orderBoards.getPageable().getPageNumber() + 4);

        orderPageDto.setOrderBoards(orderBoards);
        orderPageDto.setHomeStartPage(homeStartPage);
        orderPageDto.setHomeEndPage(homeEndPage);

        return orderPageDto;
    }

    @Override
    public OrderPageDto findAllOrderByConditionByPaging(Long marketId, SearchOrder searchOrder, Pageable pageable) {
        OrderPageDto orderPageDto = new OrderPageDto();

        Page<OrderDto> orderBoards = orderRepository.searchAllOrderByConditionAndMarketId(marketId, searchOrder, pageable);
        int startPage = Math.max(1, orderBoards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(orderBoards.getTotalPages(), orderBoards.getPageable().getPageNumber() + 4);

        orderPageDto.setOrderBoards(orderBoards);
        orderPageDto.setHomeStartPage(startPage);
        orderPageDto.setHomeEndPage(endPage);

        return orderPageDto;
    }

    @Override
    @Transactional
    public Long changeOrderStatus(Long id, OrderStatus status, PostCompany postCompany, String postNumber) {
        Optional<OrderItem> findOrderItem = orderItemRepository.findById(id);
        OrderItem checkedOrderItem = new OrderItem();
        if (findOrderItem.isPresent()) {
            checkedOrderItem = findOrderItem.get();
        }
        checkedOrderItem.setOrderStatus(status);
        checkedOrderItem.setPostCompany(postCompany);
        checkedOrderItem.setPostNumber(postNumber);

        return checkedOrderItem.getId();
    }
}
