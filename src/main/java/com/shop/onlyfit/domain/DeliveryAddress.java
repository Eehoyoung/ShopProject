package com.shop.onlyfit.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_address_id")
    private Long id;

    private String placeName;

    private String recipient;

    private String city;

    private String street;

    private String zipcode;

    private String addressHomePhoneNumber;

    private String addressPhoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //연관관계 메소드
    public void setUser(User user) {
        this.user = user;
        user.getDeliveryAddressList().add(this);
    }
}
