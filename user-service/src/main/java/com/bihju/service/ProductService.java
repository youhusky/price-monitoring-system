package com.bihju.service;

import com.bihju.domain.Product;

import java.util.List;

public interface ProductService {
    List<Product> findProduct(long categoryId);
}
