package com.shop.onlyfit.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String itemName;
    private String firstCategory;
    private String secondCategory;
    private int price;
    private String saleStatus;
    private String imgUrl;
    private String color;
    private boolean rep;
    private Long itemIdx;
    private int cartConunt;

    @QueryProjection
    public ItemDto(Long id, String itemName, String firstCategory, int price, String saleStatus, String imgUrl, String color, boolean rep, Long itemIdx) {

        this.id = id;
        this.itemName = itemName;
        this.firstCategory = firstCategory;
        this.price = price;
        this.saleStatus = saleStatus;
        this.imgUrl = imgUrl;
        this.color = color;
        this.rep = rep;
        this.itemIdx = itemIdx;
    }

    @QueryProjection
    public ItemDto(Long itemIdx, String itemName, String imgUrl, int price, String firstCategory, String secondCategory, String saleStatus, boolean rep) {

        this.itemName = itemName;
        this.firstCategory = firstCategory;
        this.secondCategory = secondCategory;
        this.price = price;
        this.saleStatus = saleStatus;
        this.imgUrl = imgUrl;
        this.rep = rep;
        this.itemIdx = itemIdx;
    }

    @QueryProjection
    public ItemDto(Long itemIdx, String imgUrl, String itemName, String color, int price, int cartConunt) {
        this.itemName = itemName;
        this.price = price;
        this.imgUrl = imgUrl;
        this.color = color;
        this.itemIdx = itemIdx;
        this.cartConunt = cartConunt;
    }
}
