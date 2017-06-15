package com.bihju.rest;

import com.bihju.ProductProcessor;
import com.bihju.domain.Product;
import com.bihju.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price-monitors")
@Log4j
public class PriceMonitorApi {
    @Autowired
    private ProductProcessor productProcessor;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public String getVersion() {
        return "1.0.0";
    }

    @RequestMapping(value = "products/{priority}", method = RequestMethod.POST)
    public Product parseProduct(@PathVariable int priority, @RequestBody Product product) {
        try {
            switch (priority) {
                case ProductProcessor.PRIORITY_HIGH:
                    productProcessor.checkProductHigh(product);
                    return productService.getProduct(product.getProductId());

                case ProductProcessor.PRIORITY_MEDIUM:
                    productProcessor.checkProductMedium(product);
                    return productService.getProduct(product.getProductId());

                case ProductProcessor.PRIORITY_LOW:
                    productProcessor.checkProductLow(product);
                    return productService.getProduct(product.getProductId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
