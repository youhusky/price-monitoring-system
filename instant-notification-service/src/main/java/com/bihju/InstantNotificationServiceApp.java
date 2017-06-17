package com.bihju;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableCircuitBreaker
@SpringBootApplication
@Log4j
public class InstantNotificationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(InstantNotificationServiceApp.class, args);
    }
}
