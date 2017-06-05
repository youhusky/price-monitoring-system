package com.bihju;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j
public class InstantNotifierApp {
    public static void main(String[] args) {
        SpringApplication.run(InstantNotifierApp.class, args);
    }
}
