package com.shop.onlyfit.dto;


import com.querydsl.core.annotations.QueryProjection;
import com.shop.onlyfit.domain.CustomServiceReply;
import com.shop.onlyfit.domain.User;
import com.shop.onlyfit.domain.type.CSBoardType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class CustomServiceBoardDto {

    private int id;

    private User user;

    private String title;

    private CSBoardType boardType;

    private int count;

    private String content;

    private Timestamp createTime;

    private int secret;

    @QueryProjection
    public CustomServiceBoardDto(int id, User user, String title, CSBoardType csBoardType,
                                 int count, String content, Timestamp createTime, int secret){
        this.id = id;
        this.user = user;
        this.title = title;
        this.boardType = csBoardType;
        this.count = count;
        this.content = content;
        this.createTime = createTime;
        this.secret = secret;
    }
}
