package com.bihju.rest;

import com.bihju.ProductCrawlerTask;
import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.domain.ProductLog;
import com.bihju.queue.ProductSource;
import com.bihju.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product-crawlers")
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

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public String getVersion() {
        return "1.0.0";
    }

    @RequestMapping(value = "products/{priority}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String sendProduct(@PathVariable int priority, @RequestBody Product product) {
        productSource.sendProductToQueue(product, priority);
        return "Success";
    }

    @RequestMapping(value = "products/log", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String sendLog(@RequestBody ProductLog productLog) {
        productSource.sendLogToQueue(productLog);
        return "Success";
    }

    @RequestMapping(value = "products/{priority}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String startCrawler(@PathVariable final int priority) {
        if (priority > ProductCrawlerTask.PRIORITY_LOW || priority < 1) {
            return "Invalid priority.";
        }

        Thread t = new Thread() {
            public void run() {
                switch (priority) {
                    case ProductCrawlerTask.PRIORITY_HIGH:
                        productCrawlerTask.startCrawlingHighPriority();
                        return;

                    case ProductCrawlerTask.PRIORITY_MEDIUM:
                        productCrawlerTask.startCrawlingMediumPriority();
                        return;

                    case ProductCrawlerTask.PRIORITY_LOW:
                        productCrawlerTask.startCrawlingLowPriority();
                        return;
                }
            }
        };

        t.start();

        return "Success";
    }

    @RequestMapping(value = "categories/{priority}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getCategoryByPriority(@PathVariable int priority) {
        return categoryService.getCategories(priority);
    }
}
