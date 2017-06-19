package com.bihju.service;

import com.bihju.domain.Product;

public interface ProductService {
    void createProduct(Product product);
    void updateProduct(Product product);
    Product getProduct(String productId);
    double getDiscountPercent(double oldPrice, double newPrice);
}
