package com.eggip.sai.access;


import com.eggip.sai.Status;

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
public class FieldRestrictionRule {
    private int id;
    private String target;
    private RestrictionType restrictionType;

    private String restriction;   // sql片段，需要做无法检验

    private AuthorizedType authorizedType;
    private int entityId;  
    private Status status;

    public static enum RestrictionType {
        VISIBLE,
        VALUE;
    }
     

}