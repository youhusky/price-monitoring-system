package com.bihju.repository;

import com.bihju.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "categories", collectionResourceRel = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category c, UserCategory uc " +
            "where c.id = uc.categoryId group by uc.categoryId order by sum(uc.userId) desc")
    List<Category> getAllSubscribedCategories();
}
