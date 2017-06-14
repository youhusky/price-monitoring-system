package com.bihju.rest;

import com.bihju.domain.ProductLog;
import com.bihju.service.ProductLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-log")
public class ProductLogServiceApi {
    private ProductLogService productLogService;

    @Autowired
    public ProductLogServiceApi(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    public void saveLogToDB(ProductLog productLog) {
        productLogService.saveLogToDB(productLog);
    }
}
