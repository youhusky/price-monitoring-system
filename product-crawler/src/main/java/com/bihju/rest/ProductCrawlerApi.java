package com.bihju.rest;

import com.bihju.ProductSource;
import com.bihju.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product-crawler")
public class ProductCrawlerApi {
    private ProductSource productSource;

    @Autowired
    public ProductCrawlerApi(ProductSource productSource) {
        this.productSource = productSource;
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
}
