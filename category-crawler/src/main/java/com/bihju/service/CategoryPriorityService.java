package com.bihju.service;

import com.bihju.domain.CategoryPriority;

public interface CategoryPriorityService {
    CategoryPriority getCategoryPriorityByCategoryId(long categoryId);
    void saveCategoryPriority(long categoryId, int priority);
}
