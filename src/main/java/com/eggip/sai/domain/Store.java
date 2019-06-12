package com.eggip.sai.domain;

import com.eggip.sai.access.AuthorizedType;
import com.eggip.sai.access.Target;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Entity(name = "store")
public class Store implements Target<Organization> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 1, max = 100)
    @Column(length = 100, nullable = false)
    private String name;

    @Size(min = 1, max = 30)
    @Column(name = "store_no", nullable = false, unique = true)
    private String storeNo;

    @Override
    public String to() {
        return String.format("$.id = #.entity_id and #.type = %s", AuthorizedType.STORE.ordinal());
    }

    
}