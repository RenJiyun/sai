package com.eggip.sai.service.dto;


import com.eggip.sai.domain.Component;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ComponentDTO {
    private Integer id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(min = 1, max = 200)
    private String acReactiveKey;

    @Size(max = 100)
    private String route;

    @Size(max = 100)
    private String api;

    private Integer parentId;

    @NotNull
    private Component.ForbiddenType forbiddenType;

    @NotNull
    private Component.ComponentType componentType;

    private List<ComponentDTO> children;




}
