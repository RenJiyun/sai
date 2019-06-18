package com.eggip.sai.web.rest;

import com.eggip.sai.domain.Category;
import com.eggip.sai.repository.CategoryRepository;
import com.eggip.sai.service.dto.CategoryDTO;
import com.eggip.sai.service.mapper.CategoryMapper;
import com.eggip.sai.util.LRTree;
import com.eggip.sai.web.rest.errors.BadRequestAlertException;
import com.eggip.sai.web.rest.qo.CategoryQO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CategoryResource {
    private final Logger logger = LoggerFactory.getLogger(CategoryResource.class);


    @Autowired
    private LRTree lrTree;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryRepository categoryRepository;


    /**
     * 获取整个品类树
     * 特殊api，不是很restful了 :>
     * @return
     * @throws URISyntaxException
     */
    @GetMapping(value = "/categoryTree")
    public ResponseEntity<CategoryDTO> getCategoryTree() throws URISyntaxException {
        return ResponseEntity.created(new URI("/api/categoryTree"))
                .body(
                        lrTree.getTreeSlow(Category.class).orThrow()
                                .fmap(category -> categoryMapper.categoryTreeToCategoryDTOTree(category))
                                .orElse(null)
                );
    }


    // 获取品类数据
    @GetMapping(value = "/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories(CategoryQO categoryQO) {
        throw new UnsupportedOperationException();
    }


    // 新增品类
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws URISyntaxException {
        if (categoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new category cannot already hava an ID", "categoryManagement", "id exists");
        }

        if (categoryRepository.findByCode(categoryDTO.getCode()).isPresent()) {
            throw new BadRequestAlertException("Category already exists", "categoryManagement", "exists");
        }

        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        if (StringUtils.isBlank(categoryDTO.getParentCode())) {
            if (lrTree.getRoot(Category.class).orThrow().toOptional().isPresent()) {
                throw new BadRequestAlertException("A root category already exists", "categoryManagement", "root exists");
            }

            lrTree.insert(Category.class, null, category);
        } else {
            Optional<Category> parent = categoryRepository.findByCode(categoryDTO.getParentCode());
            if (!parent.isPresent()) {
                throw new BadRequestAlertException("Parent category doesn't exist", "categoryManagement", "not found");
            }

            lrTree.insert(Category.class, parent.get(), category);
        }


        return ResponseEntity.created(new URI("/api/categories"))
                .header("categoryManagement.created", category.getCode())
                .body(category);
    }


    // 更新品类
    @PutMapping("/categories")
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody  CategoryDTO categoryDTO) throws URISyntaxException {
        if (categoryDTO.getId() == null) {
            throw new BadRequestAlertException("Category doesn't exits", "categoryManagement", "not found");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(categoryDTO.getId());
        if (!optionalCategory.isPresent()) {
            throw new BadRequestAlertException("Category doesn't exits", "categoryManagement", "not found");
        }

        Category category = optionalCategory.get();
        category.setCode(categoryDTO.getCode());
        category.setName(categoryDTO.getName());

        categoryRepository.saveAndFlush(category);

        return ResponseEntity.created(new URI("/api/categories"))
                .header("categoryManagement.updated", category.getCode())
                .body(category);
    }



}
