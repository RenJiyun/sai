package com.eggip.sai.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Goods {
    private int id;
    private String name;
    private String goodsNo;
    private BigDecimal buyPrice;
    private BigDecimal salePrice;
    private Category category;
    private Brand brand;
}