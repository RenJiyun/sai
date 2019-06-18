package com.eggip.sai.web.rest.qo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryQO extends BaseQO {
    private Integer id;
    private String name;
    private String parentCode;
}
