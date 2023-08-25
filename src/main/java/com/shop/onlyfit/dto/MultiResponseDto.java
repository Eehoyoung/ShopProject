package com.shop.onlyfit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MultiResponseDto<T> {
    private boolean success;
    private List<T> data;
    private PageInfo pageInfo;
}
