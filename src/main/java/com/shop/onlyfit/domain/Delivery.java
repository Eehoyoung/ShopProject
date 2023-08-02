package com.shop.onlyfit.domain;

import com.shop.onlyfit.domain.type.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private UserAddress userAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

}
