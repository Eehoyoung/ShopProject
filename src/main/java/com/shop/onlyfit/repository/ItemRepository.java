package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findAllByItemIdxAndColor(Long itemIdx, String color);

    List<Item> findAllByItemIdx(Long itemIdx);

    List<Item> findAllByItemIdxAndRep(Long itemIdx, boolean rep);

    Item findTopByItemIdxAndRep(Long itemIdx, boolean rep);

    Item findByItemIdxAndColorAndRep(Long itemIdx, String color, boolean rep);

}
