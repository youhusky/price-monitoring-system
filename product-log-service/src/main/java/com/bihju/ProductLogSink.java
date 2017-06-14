package com.bihju;

import com.bihju.domain.ProductLog;
import com.bihju.service.ProductLogService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
@EnableBinding(Sink.class)
@Log4j
public class ProductLogSink {
    private ProductLogService productLogService;

    @Autowired
    public ProductLogSink(ProductLogService productLogService) {
        this.productLogService = productLogService;
    }

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void saveLogToDB(ProductLog productLog) throws Exception {
        log.info("ProductLog received, status = " + productLog.getStatus() + ", message = " + productLog.getMessage());
        productLogService.saveLogToDB(productLog);
    }
}
