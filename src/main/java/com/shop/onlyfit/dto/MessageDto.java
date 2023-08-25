package com.shop.onlyfit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2082503192322391880L;
    @NotNull
    private Long roomId;
    @NotNull
    private Long senderId;
    @NotBlank
    private String nickName;  // Add this line to include the nickname in the message.
    @NotBlank
    private String content;

    private LocalDateTime sendTime;

    public void setContent(String content) {
        this.content = content;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    } // Add a setter for the new field.

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}

