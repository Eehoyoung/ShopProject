package com.shop.onlyfit.dto;

import com.shop.onlyfit.domain.Mileage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class MileagePageDto {
    Page<Mileage> mileageBoards;
    int homeStartPage;
    int homeEndPage;
}
