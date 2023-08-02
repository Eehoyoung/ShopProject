package com.shop.onlyfit.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ColumnDefault("1")
    private int cartCount;

    public void setItem(Item item) {
        this.item = item;
        item.getCartList().add(this);
    }

    public void setUser(User user) {
        this.user = user;
        user.getCartList().add(this);
    }
}
