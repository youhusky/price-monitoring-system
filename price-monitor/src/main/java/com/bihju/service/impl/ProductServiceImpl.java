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
    public void saveProduct(Product product) {
        product.setOldPrice(product.getPrice());
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Product product) {
        Product current = productRepository.findProductByProductId(product.getProductId());
        current.setOldPrice(current.getPrice());
        current.setPrice(product.getPrice());

        productRepository.save(current);
    }

    @Override
    public Product getProduct(String productId) {
        return productRepository.findProductByProductId(productId);
    }
}
