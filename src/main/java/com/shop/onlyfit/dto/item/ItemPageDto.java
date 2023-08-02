package com.shop.onlyfit.dto.item;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class ItemPageDto {

    Page<ItemDto> itemPage;
    int homeStartPage;
    int homeEndPage;
}
