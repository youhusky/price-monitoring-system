package com.bihju;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Log4j
@EnableHystrixDashboard
//@EnableCircuitBreaker
@SpringBootApplication
@EnableScheduling
@EnableAsync
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
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("CategoryCrawlerApp-");
        executor.initialize();
        return executor;
    }
}
