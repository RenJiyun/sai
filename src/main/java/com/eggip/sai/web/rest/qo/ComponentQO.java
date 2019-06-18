package com.eggip.sai.web.rest.qo;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ComponentQO extends BaseQO {
    private Integer id;
    private Integer parentId;
    private String acReactiveKey;
}
