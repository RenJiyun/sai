package com.eggip.sai.web.rest.qo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseQO {
    private Integer limit;
    private Integer offset;

    private String orderBy;

    private Order order;

    public static enum Order {
        ASC,
        DESC
    }


}
