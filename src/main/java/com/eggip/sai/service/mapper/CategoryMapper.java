package com.eggip.sai.service.mapper;

import com.eggip.sai.domain.Category;
import com.eggip.sai.service.dto.CategoryDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryMapper {

    public Category categoryDTOToCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) return null;
        return Category.builder()
                .id(categoryDTO.getId())
                .code(categoryDTO.getCode())
                .parentCode(categoryDTO.getParentCode())
                .name(categoryDTO.getName())
                .build();
    }


    public CategoryDTO categoryToCategoryDTO(Category category) {
        if (category == null) return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .parentCode(category.getParentCode())
                .build();

    }

    // 品类树转换
    public CategoryDTO categoryTreeToCategoryDTOTree(Category parent) {
        CategoryDTO dtoParent = categoryToCategoryDTO(parent);
        List<Category> children = parent.getChildren();
        if (children != null && children.size() > 0) {
            List<CategoryDTO> dtoChildren = new ArrayList<>();
            for (Category child : children) {
                CategoryDTO dtoChild = categoryTreeToCategoryDTOTree(child);
                dtoChildren.add(dtoChild);
            }
            dtoParent.setChildren(dtoChildren);
        }

        return dtoParent;
    }

}
