package com.bihju;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.service.CategoryService;
import com.bihju.util.CrawlerUtil;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Log4j
public class ProductCrawlerTask {
    public final static int PRIORITY_HIGH = 1;
    public final static int PRIORITY_MEDIUM = 2;
    public final static int PRIORITY_LOW = 3;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final static String AUTH_USER = "bittiger";
    private final static String AUTH_PASSWORD = "cs504";

    private List<String> proxyList;
    private AtomicInteger proxyIndex1 = new AtomicInteger(0);
    private AtomicInteger proxyIndex2 = new AtomicInteger(0);
    private AtomicInteger proxyIndex3 = new AtomicInteger(0);
    private Map<String, String> headers;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductSource productSource;
    @Autowired
    private AsyncTaskExecutor taskExecutor;

    public void init(String proxyFile) {
        initProxyList(proxyFile);
        initHeaders();
    }

    @Scheduled(cron = "0 0 1-22/3 * * *")   // every 3 hours, starting from 1:00 AM
    public void startCrawlingHighPriority() {
        log.info("Start crawling high priority categories, threadId: " + Thread.currentThread().getId());

        List<Category> categoryList = getCategories(PRIORITY_HIGH);
        List<String> subProxyList = proxyList.subList(0, proxyList.size() / 3);
        for (Category category : categoryList) {
            taskExecutor.submit(new ProductCrawlerWorker(category, subProxyList, proxyIndex1, headers, productSource, PRIORITY_HIGH));
            delayBetweenCrawling();
        }

        log.info("End crawling high priority categories, threadId: " + Thread.currentThread().getId());
    }

    @Scheduled(cron = "0 0 2-14/12 * * *")   // every 12 hours, starting from 2:00 AM
    public void startCrawlingMediumPriority() {
        log.info("Start crawling medium priority categories, threadId: " + Thread.currentThread().getId());

        List<Category> categoryList = getCategories(PRIORITY_MEDIUM);
        List<String> subProxyList = proxyList.subList(proxyList.size() / 3 + 1, proxyList.size() / 3 * 2);
        for (Category category : categoryList) {
            taskExecutor.submit(new ProductCrawlerWorker(category, subProxyList, proxyIndex2, headers, productSource, PRIORITY_MEDIUM));
            delayBetweenCrawling();
        }

        log.info("End crawling medium priority categories, threadId: " + Thread.currentThread().getId());
    }

    @Scheduled(cron = "0 0 3 * * *")   // every day, starting from 3:00 AM
    public void startCrawlingLowPriority() {
        log.info("Start crawling low priority categories, threadId: " + Thread.currentThread().getId());

        List<Category> categoryList = getCategories(PRIORITY_LOW);
        List<String> subProxyList = proxyList.subList(proxyList.size() / 3 * 2, proxyList.size());
        for (Category category : categoryList) {
            taskExecutor.submit(new ProductCrawlerWorker(category, subProxyList, proxyIndex3, headers, productSource, PRIORITY_LOW));
            delayBetweenCrawling();
        }

        log.info("End crawling low priority categories, threadId: " + Thread.currentThread().getId());
    }

    private List<Category> getCategories(int priority) {
        return categoryService.getCategories(priority);
    }

    private void delayBetweenCrawling() {
        try {
            Thread.sleep(20000); // wait 2 seconds before next round
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO(BJC) Extract common methods for crawling into a crawling jar for sharing.
    private void initProxyList(String proxyFilePath) {
        proxyList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(proxyFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String ip = fields[0].trim();
                proxyList.add(ip);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                AUTH_USER, AUTH_PASSWORD.toCharArray());
                    }
                }
        );

        System.setProperty("http.proxyUser", AUTH_USER);
        System.setProperty("http.proxyPassword", AUTH_PASSWORD);
        System.setProperty("socksProxyPort", "61336"); // set proxy port
    }

    private void initHeaders() {
        headers = new HashMap<>();
        headers.put("Accept", "text/html,text/plain");
        headers.put("Accept-Language", "en-us,en");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Accept-Charset", "utf-8");
    }
}
