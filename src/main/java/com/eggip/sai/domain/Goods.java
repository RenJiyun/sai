package com.eggip.sai.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "goods")
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 1, max = 100)
    @Column(length = 100, nullable = false)
    private String name;

    @Size(min = 1, max = 30)
    @Column(name = "goods_no", length = 30, nullable = false, unique = true)
    private String goodsNo;

    @DecimalMin("0")
    @Column(name = "buy_price")
    private BigDecimal buyPrice;

    @DecimalMin("0")
    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Brand brand;
}