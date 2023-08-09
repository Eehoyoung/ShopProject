package com.shop.onlyfit.dto;

import com.shop.onlyfit.domain.Market;
import com.shop.onlyfit.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MarketInfoDto {

    private Long id;

    private String name;

    private String businessNumber;

    private String storeNumber;

    private User user;

    public Market toEntity() {
        return Market.builder()
                .id(id)
                .name(name)
                .businessNumber(businessNumber)
                .storeNumber(storeNumber)
                .seller(user)
                .build();
    }
}
