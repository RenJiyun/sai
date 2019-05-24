package com.eggip.sai.domain;

import com.eggip.sai.access.AuthorizedType;
import com.eggip.sai.access.Target;

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
public class Store implements Target<Organization> {
    private int id;
    private String name;
    private String storeNo;

    @Override
    public String to() {
        return String.format("$.id = #.entity_id and #.type = %s", AuthorizedType.STORE.ordinal());
    }

    
}