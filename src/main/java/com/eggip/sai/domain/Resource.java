package com.eggip.sai.domain;


import com.eggip.sai.repository.MenuRepository;
import com.eggip.sai.util.LRTree;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import javax.validation.constraints.Size;


/**
 * 前端交互组件
 * 需要完成两件事情：
 * 1.后端模型可以直接映射为前端简单的增删改查界面，加快开发速度
 * 2.该模型将支撑功能权限的构建
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Entity(name = "resource")
public class Resource extends LRTree.LRTreeNode<Resource> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 仅用于界面显示，无实际意义
    @Size(min = 1, max = 50)
    @Column(length = 50, nullable = false)
    private String name;

    @Size(min = 1, max = 200)
    @Column(length = 200, nullable = false, unique = true)
    private String key;

    // 如果点击该组件后将进行路由切换，则该组件将关联该路由信息
    @Size(min = 1, max = 100)
    @Column(length = 100)
    private String route;

    @Size(min = 1, max = 100)
    @Column(length = 100)
    private String api;

    @Column(name = "parent_id", nullable = false)
    private int parentId;

    @Column(name = "forbidden_type")
    private ForbiddenType forbiddenType;

    // 仅用于界面显示，无实际意义
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;


    @Override
    protected Class<? extends JpaRepository<Resource, ?>> getCargoRepositoryClass() {
        return MenuRepository.class;
    }


    public static enum ForbiddenType {
        DISABLE,
        HIDE
    }

    public static enum ResourceType {
        MENU,
        PAGE,
        BUTTON
    }

}

