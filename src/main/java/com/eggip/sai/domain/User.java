package com.eggip.sai.domain;

import com.eggip.sai.access.AuthorizedType;
import com.eggip.sai.access.Target;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Entity(name = "user")
public class User implements Target<Organization> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "user_no", length = 30, unique = true, nullable = false)
    private String userNo;

    @Size(min = 1, max = 50)
    @Column(name = "user_name", length = 50)
    private String userName;

    @Size(min = 11, max=20)
    @Column(length = 20, nullable = false)
    private String phone;

    @Size(min = 1, max = 30)
    @Column(name = "open_id", length = 30)
    private String openId;

    @Size(min = 1, max = 50)
    @Column(length = 50, nullable = false)
    private String password;

    @Override
    public String to() {
        return String.format("$.id = #.entity_id and #.type = %s", AuthorizedType.USER.ordinal());
    }

}