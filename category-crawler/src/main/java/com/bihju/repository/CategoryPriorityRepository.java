package com.bihju.repository;

import com.bihju.domain.CategoryPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "category_priority", collectionResourceRel = "category_priority")
public interface CategoryPriorityRepository extends JpaRepository<CategoryPriority, Long> {
    @RestResource(path = "categoryId", rel = "by-categoryId")
    CategoryPriority findCategoryPriorityByCategoryId(long categoryId);
}

