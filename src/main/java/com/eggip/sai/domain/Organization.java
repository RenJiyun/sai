package com.eggip.sai.domain;

import com.eggip.sai.access.AuthorizedType;
import com.eggip.sai.repository.OrganizationRepository;
import com.eggip.sai.util.LRTree.LRTreeNode;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "organization")
public class Organization extends LRTreeNode<Organization> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 1, max = 100)
    @Column(length = 100, nullable = false)
    private String name;

    // 对系统而言，以下这三个字段没什么作用
    @Size(min = 1, max = 30)
    @Column(length = 30)
    private String code;

    @Size(min = 1, max = 30)
    @Column(length = 30)
    private String parentCode;

    @Column(name = "parent_id")
    private int parentId;

    // 连接在树上的实体id
    @Column(name = "entity_id")
    private int entityId;

    @Column(name = "type")
    private AuthorizedType type;



    @Override
    protected Class<? extends JpaRepository<Organization, ?>> getCargoRepositoryClass() {
        return OrganizationRepository.class;
    }


   

}