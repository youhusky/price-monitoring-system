package com.bihju;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j
@EnableCircuitBreaker
@SpringBootApplication
@EnableScheduling
public class CategoryCrawlerApp implements CommandLineRunner {
    private CategoryCrawlerTask categoryCrawlerTask;

    @Autowired
    public CategoryCrawlerApp(CategoryCrawlerTask categoryCrawlerTask) {
        this.categoryCrawlerTask = categoryCrawlerTask;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: CategoryCrawler <proxyFilePath>");
            return;
        }

        SpringApplication.run(CategoryCrawlerApp.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        categoryCrawlerTask.init(strings[0]);
//        categoryCrawlerTask.startCrawling();
    }
}
