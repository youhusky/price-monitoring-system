package com.bihju.rest;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.service.CategoryService;
import com.bihju.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deal-search-service")
public class DealSearchServiceApi {
    private ProductService productService;
    private CategoryService categoryService;

    @Autowired
    public DealSearchServiceApi(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "deals/{categoryId}", method = RequestMethod.GET)
    public List<Product> search(@PathVariable long categoryId) {
        return productService.findProduct(categoryId);
    }

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }
}
