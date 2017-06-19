package com.bihju.repository;

import com.bihju.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "products", collectionResourceRel = "products")
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    @Query("select p from Product p " +
            "where p.price < p.oldPrice and p.discountPercent >= :minDiscountPercent and p.discountPercent <> 0 and p.price <> 0")
    Page<Product> findAll(@Param("minDiscountPercent") Double minDiscountPercent, Pageable pageable);

    @Query("select p from Product p " +
            "where p.categoryId = :categoryId and p.price < p.oldPrice and p.discountPercent >= :minDiscountPercent and p.discountPercent <> 0 and p.price <> 0")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, @Param("minDiscountPercent") Double minDiscountPercent, Pageable pageable);
}
