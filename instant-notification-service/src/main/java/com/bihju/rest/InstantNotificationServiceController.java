package com.bihju.rest;

import com.bihju.ReducedProductSink;
import com.bihju.domain.Product;
import com.bihju.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instant-notifications")
public class InstantNotificationServiceController {
    private ReducedProductSink reducedProductSink;
    private UserService userService;

    @Autowired
    public InstantNotificationServiceController(ReducedProductSink reducedProductSink,
                                                UserService userService) {
        this.reducedProductSink = reducedProductSink;
        this.userService = userService;
    }

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public String getVersion() {
        return "1.0.0";
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

    @RequestMapping(value="emails", method= RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<String> findEmailsByCategoryId(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                               @RequestParam(value = "discountPercent", required = false, defaultValue = "0.0") Double discountPercent) {
        try {
            return userService.findUsersByCategoryId(categoryId, discountPercent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
