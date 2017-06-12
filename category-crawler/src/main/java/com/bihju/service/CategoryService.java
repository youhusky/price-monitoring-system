package com.bihju.service;

import com.bihju.domain.Category;

import java.util.List;

public interface CategoryService {
    void saveCategory(String categoryName, String productListUrl);
    List<Category> getSortedCategories();
}
