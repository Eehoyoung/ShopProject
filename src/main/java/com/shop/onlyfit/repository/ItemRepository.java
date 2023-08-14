package com.shop.onlyfit.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.shop.onlyfit.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findAllByItemIdxAndColor(Long itemIdx, String color);

    List<Item> findAllByItemIdx(Long itemIdx);

    List<Item> findAllByItemIdxAndRep(Long itemIdx, boolean rep);

    Item findTopByItemIdxAndRep(Long itemIdx, boolean rep);

    @Query("SELECT i FROM Item i WHERE i.itemIdx = :itemIdx AND i.color = :color AND i.rep = :rep")
    Item findByItemIdxAndColorAndRep(@Param("itemIdx") Long itemIdx,@Param("color") String color,@Param("rep") boolean rep);

    @Query("SELECT i.stackQuantity from Item i where i.id = :id")
    int findMaxItemQuantity(@Param("id") Long id);

    @Query("select i.market.marketId FROM Item i WHERE i.itemIdx = :itemIdx")
    Long findMarketIdByItemIdx(@Param("itemIdx") Long itemIdx);
}
