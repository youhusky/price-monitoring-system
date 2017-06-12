package com.bihju.service.impl;

import com.bihju.domain.CategoryPriority;
import com.bihju.repository.CategoryPriorityRepository;
import com.bihju.service.CategoryPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryPriorityServiceImpl implements CategoryPriorityService {
    private CategoryPriorityRepository categoryPriorityRepository;

    @Autowired
    public CategoryPriorityServiceImpl(CategoryPriorityRepository categoryPriorityRepository) {
        this.categoryPriorityRepository = categoryPriorityRepository;
    }

    @Override
    public CategoryPriority getCategoryPriorityByCategoryId(long categoryId) {
        return categoryPriorityRepository.findCategoryPriorityByCategoryId(categoryId);
    }

    @Override
    public void saveCategoryPriority(long categoryId, int priority, long userCount) {
        CategoryPriority categoryPriority = categoryPriorityRepository.findCategoryPriorityByCategoryId(categoryId);
        if (categoryPriority != null) {
            categoryPriority.setPriority(priority);
            categoryPriority.setUserCount(userCount);
            categoryPriority.setUpdateTime(System.currentTimeMillis());
            categoryPriorityRepository.save(categoryPriority);
        } else {
            categoryPriority = new CategoryPriority(categoryId, priority, userCount);
            categoryPriority.setCreateTime(System.currentTimeMillis());
            categoryPriority.setUpdateTime(System.currentTimeMillis());
            categoryPriorityRepository.save(categoryPriority);
        }
    }
}
