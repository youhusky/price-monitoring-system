package com.bihju;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@EnableCircuitBreaker
@Log4j
@SpringBootApplication
public class ProductCrawlerApp implements CommandLineRunner {
    private ProductCrawlerTask productCrawlerTask;

    @Autowired
    public ProductCrawlerApp(ProductCrawlerTask productCrawlerTask) {
        this.productCrawlerTask = productCrawlerTask;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("Usage: ProductCrawler <proxyFilePath>");
            return;
        }

        SpringApplication.run(ProductCrawlerApp.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        productCrawlerTask.init(strings[0]);
//        productCrawlerTask.startCrawling();
    }
}
