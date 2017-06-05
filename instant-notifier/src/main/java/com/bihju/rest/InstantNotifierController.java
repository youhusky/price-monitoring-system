package com.bihju.rest;

import com.bihju.ReducedProductSink;
import com.bihju.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InstantNotifierController {
    private ReducedProductSink reducedProductSink;

    @Autowired
    public InstantNotifierController(ReducedProductSink reducedProductSink) {
        this.reducedProductSink = reducedProductSink;
    }

    @RequestMapping(name="products", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendMail(@RequestBody Product product) {
        try {
            reducedProductSink.processProducts(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
