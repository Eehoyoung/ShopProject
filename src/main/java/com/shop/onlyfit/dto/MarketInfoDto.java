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

    private Long marketId;

    private String name;

    private String businessNumber;

    private String storeNumber;

    private User user;

    private int visitCount;

    public Market toEntity() {
        return Market.builder()
                .marketId(marketId)
                .name(name)
                .businessNumber(businessNumber)
                .storeNumber(storeNumber)
                .seller(user)
                .visitCount(visitCount)
                .build();
    }
}
