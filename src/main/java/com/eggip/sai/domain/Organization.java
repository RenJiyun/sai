package com.eggip.sai.domain;

import com.eggip.sai.access.AuthorizedType;
import com.eggip.sai.repository.OrganizationRepository;
import com.eggip.sai.util.LRTree.LRTreeNode;

import org.springframework.data.jpa.repository.JpaRepository;

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
public class Organization extends LRTreeNode<Organization> {
    private int id;
    private String name;

    // 对系统而言，以下这三个字段没什么作用
    private String code;
    private String parentCode;
    private int parentId;

    // 连接在树上的实体id
    private int entityId;
    private AuthorizedType type;



    @Override
    protected Class<? extends JpaRepository<Organization, ?>> getCargoRepositoryClass() {
        return OrganizationRepository.class;
    }


   

}