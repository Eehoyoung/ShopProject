package com.shop.onlyfit.dto.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemToCartDto {
    private Long id;
    private String item_color;
    private String item_idx;
    private String where;
    private String quantity;
}
