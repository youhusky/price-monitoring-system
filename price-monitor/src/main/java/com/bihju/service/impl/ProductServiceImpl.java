package com.bihju.service.impl;

import com.bihju.domain.Product;
import com.bihju.repository.ProductRepository;
import com.bihju.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void createProduct(Product product) {
        product.setOldPrice(product.getPrice());
        product.setCreateTime(System.currentTimeMillis());
        product.setUpdateTime(System.currentTimeMillis());
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Product product) {
        Product current = productRepository.findProductByProductId(product.getProductId());
        current.setOldPrice(product.getOldPrice());
        current.setPrice(product.getPrice());
        current.setUpdateTime(System.currentTimeMillis());
        productRepository.save(current);
    }

    @Override
    public Product getProduct(String productId) {
        return productRepository.findProductByProductId(productId);
    }
}
