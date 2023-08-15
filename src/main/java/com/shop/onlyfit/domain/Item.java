package com.shop.onlyfit.domain;

import com.shop.onlyfit.exception.NotEnoughStackException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String firstCategory;

    private String secondCategory;

    private String thirdCategory;

    private String itemName;

    private int price;

    @Column(columnDefinition = "TEXT")
    private String itemInfo;

    private String color;

    private String favric;

    private String model;

    private String size;

    private int stackQuantity;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    private String saleStatus;

    private Long itemIdx;

    private boolean rep;

    @ManyToOne(fetch = FetchType.LAZY)
    private Market market;

    private int saleCount;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Cart> cartList = new ArrayList<>();

    public Item(String firstCategory, String secondCategory, String thirdCategory, String itemName, int price, String itemInfo, String color, String fabric, String model, String size, int stockQuantity, String imgUrl, String saleStatus, Long itemIdx, Boolean rep, Market market) {
        this.firstCategory = firstCategory;
        this.secondCategory = secondCategory;
        this.thirdCategory = thirdCategory;
        this.itemName = itemName;
        this.price = price;
        this.itemInfo = itemInfo;
        this.color = color;
        this.favric = fabric;
        this.model = model;
        this.size = size;
        this.stackQuantity = stockQuantity;
        this.imgUrl = imgUrl;
        this.saleStatus = saleStatus;
        this.itemIdx = itemIdx;
        this.rep = rep;
        this.market = market;
    }

    public void plusStackQuantity(int plusQuantity) {
        this.stackQuantity += plusQuantity;
    }

    public void minusStackQuantity(int minusQuantity) {
        int resultStack = this.stackQuantity - minusQuantity;
        if (resultStack < 0) {
            throw new NotEnoughStackException("재고가 없습니다.");
        } else {
            this.stackQuantity = resultStack;
        }
    }
}
