package com.bihju;

import lombok.extern.log4j.Log4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Log4j
public class CategoryCrawlerTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 0 * * SUN")   // every Sunday
    public void startCrawling() {
        log.info("Start crawling at: " + dateFormat.format(new Date()));
    }
}
