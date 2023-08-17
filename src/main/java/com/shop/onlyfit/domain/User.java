package com.shop.onlyfit.domain;

import com.shop.onlyfit.domain.type.LoginType;
import com.shop.onlyfit.domain.type.UserGrade;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.util.annotation.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String loginId;

    private String password;

    @Column
    private String name;

    private String sex;

    private String email;

    private String birthday;

    @Enumerated(EnumType.STRING)
    private UserGrade userGrade;

    @Embedded
    private UserAddress userAddress;

    private int visitCount;

    private int orderCount;

    @Column
    private String phoneNumber;

    @Nullable
    private String homePhoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Mileage> mileageList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DeliveryAddress> deliveryAddressList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cart> cartList = new ArrayList<>();

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
    private Market market;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    public User(String name, String loginId, String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }

    @Builder
    public User(Long id, String loginId, String password, String name, String homePhoneNumber, String phoneNumber, String email, String birthday, UserGrade userGrade, LoginType loginType) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.homePhoneNumber = homePhoneNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthday = birthday;
        this.userGrade = userGrade;
        this.loginType = loginType;
    }


}
