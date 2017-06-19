package com.bihju.service.impl;

import com.bihju.domain.Product;
import com.bihju.repository.ProductRepository;
import com.bihju.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> searchDeals(Long categoryId, Double minDiscountPercent, Pageable pageable) {
        if (categoryId == null) {
            return productRepository.findAll(minDiscountPercent, pageable);
        } else {
            return productRepository.findByCategoryId(categoryId, minDiscountPercent, pageable);
        }
    }
}
