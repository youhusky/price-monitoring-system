package com.bihju.repository;

import com.bihju.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "products", collectionResourceRel = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    @RestResource(path = "categoryId", rel = "by-categoryId")
    List<Product> findProductByCategoryId(long categoryId);
}
