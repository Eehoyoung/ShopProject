package com.shop.onlyfit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class CustomServiceReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customServiceBoardId")
    private CustomServiceBoard customServiceBoard;

    @Lob
    private String content;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private Timestamp createTime;

}