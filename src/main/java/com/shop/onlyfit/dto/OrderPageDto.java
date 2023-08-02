package com.shop.onlyfit.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class OrderPageDto {
    Page<OrderDto> orderBoards;
    int homeStartPage;
    int homeEndPage;
}
