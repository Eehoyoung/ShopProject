package com.shop.onlyfit.dto.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemToCartDto {
    private Long id;
    private String itemColor;
    private String itemIdx;
    private String where;
    private String quantity;
}
