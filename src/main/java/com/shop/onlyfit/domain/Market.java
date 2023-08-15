package com.shop.onlyfit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    private Long marketId;

    @Column(name = "market_name", unique = true)
    private String name;

    @Column
    private String businessNumber;

    @Column
    private String storeNumber;

    @Column

    private int visitCount;

    @OneToMany(mappedBy = "market")
    private List<Item> items = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private User seller;

    public Market(String name, User seller) {
        this.name = name;
        this.seller = seller;
    }

    @Builder
    public Market(Long marketId, String name, String businessNumber, String storeNumber, User seller, int visitCount) {
        this.marketId = marketId;
        this.name = name;
        this.businessNumber = businessNumber;
        this.storeNumber = storeNumber;
        this.seller = seller;
        this.visitCount = visitCount;
    }
}
