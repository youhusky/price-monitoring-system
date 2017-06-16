package com.bihju.repository;

import com.bihju.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "products", collectionResourceRel = "products")
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    @Query("select p from Product p " +
            "where p.price < p.oldPrice")
    Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p " +
            "where p.categoryId = :categoryId and p.price < p.oldPrice")
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
