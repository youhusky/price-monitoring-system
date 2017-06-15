package com.bihju.rest;

import com.bihju.domain.ProductLog;
import com.bihju.service.ProductLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product-loggers")
public class ProductLogServiceApi {
    private ProductLogService productLogService;

    @Autowired
    public ProductLogServiceApi(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public String getVersion() {
        return "1.0.0";
    }

    @RequestMapping(value = "logs", method = RequestMethod.POST)
    public String saveLogToDB(@RequestBody ProductLog productLog) {
        productLogService.saveLogToDB(productLog);
        return "Success";
    }
}
