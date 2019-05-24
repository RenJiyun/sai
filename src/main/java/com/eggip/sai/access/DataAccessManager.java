package com.eggip.sai.access;

import com.eggip.sai.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 针对不同的api设置不同的数据权限机制
 * 实现：
 * 用户查询的实体是否有一条归属路径到达规则表
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class DataAccessManager {
    private int id;
    private String api;
    private RestrictionPolicyType restrictionPolicyType;
    private String ruleTable;
    private Status status;


    


    public static enum RestrictionPolicyType {
        HIERARCHY,
        FIELD;
    }


    
}