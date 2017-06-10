package com.bihju.rest;

import com.bihju.ProductCrawlerTask;
import com.bihju.ProductSource;
import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product-crawler")
public class ProductCrawlerApi {
    private ProductSource productSource;
    private ProductCrawlerTask productCrawlerTask;
    private CategoryService categoryService;

    @Autowired
    public ProductCrawlerApi(ProductSource productSource, ProductCrawlerTask productCrawlerTask, CategoryService categoryService) {
        this.productSource = productSource;
        this.productCrawlerTask = productCrawlerTask;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        return "Success";
    }

    @RequestMapping(value = "products", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String sendProduct(@RequestBody Product product) {
        productSource.sendProductToQueue(product);
        return "Success";
    }

    @RequestMapping(value = "crawler", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String startCrawler() {
        productCrawlerTask.startCrawling();
        return "Success";
    }

    @RequestMapping(value = "subscribed-categories", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllSubscribedCategories() {
        return categoryService.getAllSubscribedCategories();
    }

}
