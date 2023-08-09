package com.shop.onlyfit.domain;

import com.shop.onlyfit.domain.type.LoginType;
import com.shop.onlyfit.domain.type.UserGrade;
import jdk.jshell.Snippet;
import lombok.*;

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
    private Long id;

    @Column(name = "market_name", unique = true)
    private String name;

    @Column
    private String businessNumber;

    @Column
    private String storeNumber;

    @OneToMany(mappedBy = "market")
    private List<Item> items = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private User seller;

    public Market(String name, User seller) {
        this.name = name;
        this.seller = seller;
    }

    @Builder
    public Market(Long id, String name, String businessNumber, String storeNumber, User seller) {
        this.id = id;
        this.name = name;
        this.businessNumber = businessNumber;
        this.storeNumber = storeNumber;
        this.seller = seller;
    }
}
