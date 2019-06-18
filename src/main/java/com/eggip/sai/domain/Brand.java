package com.eggip.sai.domain;


import com.eggip.sai.access.Source;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "brand")
public class Brand implements Source<Goods> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 1, max = 100)
    @Column(length = 100, nullable = false)
    private String name;

    @Size(min = 1, max = 30)
    @Column(name = "brand_no", length = 30, nullable = false, unique = true)
    private String brandNo;

    @Override
    public String from() {
        return "$.brand_id = #.id";
    }

   
}
