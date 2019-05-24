package com.eggip.sai.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 描述实体之间的归属关系
 * 结合组织架构树，可以形成以组织架构树为中心的数据权限控制
 * @see Source<T>
 * @see Target<T>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class BelongsTo {
    private int id;
    private String source;     
    private String target;
    private String link;
}