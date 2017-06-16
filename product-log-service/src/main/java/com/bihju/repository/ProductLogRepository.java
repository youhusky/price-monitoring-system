package com.bihju.repository;

import com.bihju.domain.ProductLog;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "product_log")
public interface ProductLogRepository extends Repository<ProductLog, Long> {
    public void save(ProductLog productLog);
}
