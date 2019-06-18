package com.eggip.sai.domain;

import com.eggip.sai.access.Source;
import com.eggip.sai.repository.CategoryRepository;
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
@Table(name = "category")
public class Category extends LRTreeNode<Category> implements Source<Goods> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 1, max = 60)
    @Column(length = 60, nullable = false)
    private String name;


    @Size(min = 1, max = 30)
    @Column(length = 30, nullable = false, unique = true)
    private String code;

    @Size(min = 1, max = 30)
    @Column(length = 30)
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