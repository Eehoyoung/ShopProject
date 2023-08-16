package com.shop.onlyfit.dto;

import com.shop.onlyfit.dto.item.ItemDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class CsPageDto {

    Page<CustomServiceBoardDto> csPage;
    int homeStartPage;
    int homeEndPage;
}
