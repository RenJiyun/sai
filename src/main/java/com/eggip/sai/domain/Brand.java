package com.eggip.sai.domain;



import com.eggip.sai.access.Source;

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
public class Brand implements Source<Goods> {
    private int id;
    private String name;
    private String brandNo;

    @Override
    public String from() {
        return "$.brand_id = #.id";
    }

   
}
