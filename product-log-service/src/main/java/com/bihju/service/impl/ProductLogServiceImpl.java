package com.bihju.service.impl;

import com.bihju.domain.ProductLog;
import com.bihju.repository.ProductLogRepository;
import com.bihju.service.ProductLogService;
import lombok.extern.log4j.Log4j;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class ProductLogServiceImpl implements ProductLogService {
    private ProductLogRepository productLogRepository;

    @Autowired
    public ProductLogServiceImpl(ProductLogRepository productLogRepository) {
        this.productLogRepository = productLogRepository;
    }

    @Override
    public void saveLogToDB(ProductLog productLog) {
        try {
            productLogRepository.save(productLog);
        } catch (DataException e) {
            e.printStackTrace();
            log.error("Failed to save to DB, message = " + productLog.getMessage());

        }
    }
}
