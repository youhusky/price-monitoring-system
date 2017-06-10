package com.bihju.service.impl;

import com.bihju.domain.Category;
import com.bihju.repository.CategoryRepository;
import com.bihju.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllSubscribedCategories() {
        return categoryRepository.getAllSubscribedCategories();
    }
}
