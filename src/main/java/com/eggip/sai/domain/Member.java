package com.eggip.sai.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.eggip.sai.access.Target;

import org.apache.catalina.Store;

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
public class Member implements Target<Store> {
    private int id;
    private String name;
    private String phone;
    private String password;
    private String openId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "register_store_id")
    private Store registerStore;

    private Date registerTime;
    private RegisterChannel registerChannel;
    private Grade grade;

    public static enum RegisterChannel {
        POS, APP, OTHER;
    }

    public static enum Grade {
        A, B, C;
    }

    @Override
    public String to() {
        return "$.register_store_id = #.id";
    }

    


   


}