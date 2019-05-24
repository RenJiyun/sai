package com.eggip.sai.domain;

import com.eggip.sai.access.Source;
import com.eggip.sai.repository.CategoryRepository;
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
public class Category extends LRTreeNode<Category> implements Source<Goods> {
    private int id;
    private String name;
    private String code;
    private String parentCode;


    @Override
    protected Class<? extends JpaRepository<Category, ?>> getCargoRepositoryClass() {
        return CategoryRepository.class;
    }

    @Override
    public String from() {
        return "$.category_id = #.id";
    }

}