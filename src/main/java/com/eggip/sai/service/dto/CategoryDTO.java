package com.eggip.sai.service.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDTO {
    private Integer id;

    @NotBlank
    @Size(min = 1, max = 60)
    private String name;


    @NotBlank
    @Size(min = 1, max = 30)
    private String code;

    @NotBlank
    @Size(max = 30)
    private String parentCode;


    private List<CategoryDTO> children;


}
