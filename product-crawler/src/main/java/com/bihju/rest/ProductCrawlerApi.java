package com.bihju.rest;

import com.bihju.domain.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ProductCrawlerApi {

    @ResponseStatus(HttpStatus.OK)
    public void sendProduct(@RequestBody Product product) {

    }
}
