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

    @Query(value="select * from category c left join user_category uc on uc.category_id = c.id group by c.id order by count(uc.user_id) desc", nativeQuery=true)
    List<Category> getSortedCategories();
}

