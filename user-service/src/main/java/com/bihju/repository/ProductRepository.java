package com.bihju.repository;

import com.bihju.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(path = "products", collectionResourceRel = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    @RestResource(path = "categoryId", rel = "by-categoryId")
    @Query("select p from Product p " +
            "where p.categoryId = :categoryId and p.price < p.oldPrice order by (p.oldPrice - p.price) desc")
    List<Product> getDiscountProducts(@Param("categoryId") long categoryId);
}
