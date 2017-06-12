package com.bihju.repository;

import com.bihju.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "categories", collectionResourceRel = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @RestResource(path = "categoryName", rel = "by-categoryName")
    Category findCategoryByCategoryName(String categoryName);

    @Query(value =
            "select c.id, count(user_id) as user_count " +
            "from category c " +
            "left join user_category uc " +
            "on uc.category_id = c.id " +
            "group by (uc.category_id) " +
            "having count(user_id) >= 1", nativeQuery = true)
    List<Object[]> getHighPriorityCategories();

    @Query(value=
            "select c.id, user_count " +
            "from category c " +
            "left join " +
            "(" +
                "select category_id, count(user_id) as user_count " +
                "from user_category uc " +
                "group by category_id " +
            ") as ucc " +
            "on ucc.category_id = c.id " +
            "where user_count < 1 or user_count is null "
            , nativeQuery=true)
    List<Object[]> getSortedCategories();
}

