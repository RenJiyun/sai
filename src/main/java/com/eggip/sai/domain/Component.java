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
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "component")
public class Component extends LRTree.LRTreeNode<Component> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 仅用于界面显示，无实际意义
    @Size(min = 1, max = 50)
    @Column(length = 50, nullable = false)
    private String name;


    // 该字段须和前端的名字一致，并且全局唯一
    @Size(min = 1, max = 200)
    @Column(name = "ac_reactive_key", length = 200, nullable = false, unique = true)
    private String acReactiveKey;

    // 如果点击该组件后将进行路由切换，则该组件将关联该路由信息
    // 并非每个组件都拥有路由信息
    @Size(max = 100)
    @Column(length = 100)
    private String route;


    // 离调用api最近的组件才会拥有该字段信息
    @Size(max = 100)
    @Column(length = 100)
    private String api;

    @Column(name = "parent_id")
    private Integer parentId;


    // 禁用类型，目前只支持隐藏
    @Column(name = "forbidden_type")
    private ForbiddenType forbiddenType;

    // 仅用于界面显示，无实际意义
    @Column(name = "component_type", nullable = false)
    private ComponentType componentType;


    @Override
    protected Class<? extends JpaRepository<Component, ?>> getCargoRepositoryClass() {
        return MenuRepository.class;
    }


    public static enum ForbiddenType {
        DISABLE,
        HIDE
    }

    public static enum ComponentType {
        MENU,
        PAGE,
        BUTTON
    }

}

