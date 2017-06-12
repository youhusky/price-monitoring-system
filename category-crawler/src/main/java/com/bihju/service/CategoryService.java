package com.bihju.service;

import java.util.List;

public interface CategoryService {
    void saveCategory(String categoryName, String productListUrl);
    List<Object[]> getHighPriorityCategories(int userCountThreshold);
    List<Object[]> getSortedCategories(int userCountThreshold);
}
