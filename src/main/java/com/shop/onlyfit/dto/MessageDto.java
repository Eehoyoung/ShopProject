package com.shop.onlyfit.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // JSON 직렬, 역직렬화를 무시
public class MessageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 2082503192322391880L;
    @NotNull
    private Long roomId;
    @NotNull
    private Long senderId;
    @NotBlank
    private String nickName;
    @NotBlank
    private String content;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sendTime;

}

