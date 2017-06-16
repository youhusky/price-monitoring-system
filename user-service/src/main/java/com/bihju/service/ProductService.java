package com.bihju.service;

import com.bihju.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> searchDeals(Long categoryId, Pageable pageable);
}
