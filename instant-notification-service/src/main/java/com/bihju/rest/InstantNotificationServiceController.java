package com.bihju.rest;

import com.bihju.ReducedProductSink;
import com.bihju.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instant-notification")
public class InstantNotificationServiceController {
    private ReducedProductSink reducedProductSink;

    @Autowired
    public InstantNotificationServiceController(ReducedProductSink reducedProductSink) {
        this.reducedProductSink = reducedProductSink;
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        return "Success";
    }

    @RequestMapping(value="products", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String sendMail(@RequestBody Product product) {
        try {
            reducedProductSink.processProductsHigh(product);
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
