package com.bihju.service.impl;

import com.bihju.domain.Category;
import com.bihju.repository.CategoryRepository;
import com.bihju.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void saveCategory(String categoryName, String productListUrl) {
        Category category = categoryRepository.findCategoryByCategoryName(categoryName);
        if (category != null) {
            category.setProductListUrl(productListUrl);
            categoryRepository.save(category);
        } else {
            category = new Category(categoryName, productListUrl);
            categoryRepository.save(category);
        }
    }
}
