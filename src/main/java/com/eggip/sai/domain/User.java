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
public class User implements Target<Organization> {
    private int id;
    private String userNo;
    private String userName;
    private String phone;
    private String openId;
    private String password;

    @Override
    public String to() {
        return String.format("$.id = #.entity_id and #.type = %s", AuthorizedType.USER.ordinal());
    }

}