package com.bihju.repository;

import com.bihju.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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
            "having count(user_id) >= " +
            "( " +
                    "select high_priority_user_count " +
                    "from  user_count_threshold " +
                    "order by update_time desc " +
                    "limit 1 " +
            ")", nativeQuery = true)
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
            "where user_count is null or user_count < " +
            "( " +
                    "select high_priority_user_count " +
                    "from  user_count_threshold " +
                    "order by update_time desc " +
                    "limit 1 " +
            ")", nativeQuery = true)
    List<Object[]> getSortedCategories();
}

