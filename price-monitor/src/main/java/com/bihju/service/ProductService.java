package com.bihju.service;

import com.bihju.domain.Product;

public interface ProductService {
    void saveProduct(Product product);
    void updateProduct(Product product);
    Product getProduct(String productId);
}
