package com.eggip.sai.domain;

import com.eggip.sai.access.Target;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member")
public class Member implements Target<Store> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 1, max = 50)
    @Column(length = 50)
    private String name;


    @Size(min = 11, max = 20)
    @Column(length = 20, nullable = false)
    private String phone;

    @Size(min = 1, max = 50)
    @Column(length = 50, nullable = false)
    private String password;

    @Size(min = 1, max = 30)
    @Column(name = "open_id", length = 30)
    private String openId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "register_store_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Store registerStore;

    @Past
    @Column(name = "register_time", nullable = false)
    private Date registerTime;

    @Column(name = "register_channel", nullable = false)
    private RegisterChannel registerChannel;

    @Column(nullable = false)
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