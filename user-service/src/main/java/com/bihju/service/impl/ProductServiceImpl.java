package com.bihju.service.impl;

import com.bihju.domain.Product;
import com.bihju.repository.ProductRepository;
import com.bihju.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findProduct(long categoryId) {
        return productRepository.getDiscountProducts(categoryId);
    }
}
