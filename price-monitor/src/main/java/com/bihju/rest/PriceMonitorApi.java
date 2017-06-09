package com.bihju.rest;

import com.bihju.ProductProcessor;
import com.bihju.domain.Product;
import com.bihju.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/price-monitor")
@Log4j
public class PriceMonitorApi {
    @Autowired
    private ProductProcessor productSink;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        return "Success";
    }

    @RequestMapping(value = "products", method = RequestMethod.POST)
    public Product parseProduct(@RequestBody Product product) {
        try {
            productSink.checkProduct(product);
            return productService.getProduct(product.getProductId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
