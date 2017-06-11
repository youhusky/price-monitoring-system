package com.bihju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@EnableCircuitBreaker
@SpringBootApplication
public class DealSearchServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(DealSearchServiceApp.class, args);
    }
}
