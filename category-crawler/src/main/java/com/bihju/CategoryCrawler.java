package com.bihju;

import com.bihju.service.CategoryService;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@SpringBootApplication
public class CategoryCrawler implements CommandLineRunner {
    private List<String> proxyList;
    private int index = 0;
    private static final String AMAZON_URL = "https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias=aps&field-keywords=-12345";
    private static final String PRODUCT_LIST_URL = "https://www.amazon.com/s/ref=nb_sb_noss?url=$SEARCH_ALIAS&field-keywords=-12345&page=$PAGE_NO";
    private static final String WHAT_IS_MY_IP_ADDRESS = "https://whatismyipaddress.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private static final String CATEGORY_SELECTOR = "#searchDropdownBox > option:nth-child($NUMBER)";
    private static final int TIMEOUT_IN_MILLISECONDS = 100000;
    private final String AUTH_USER = "bittiger";
    private final String AUTH_PASSWORD = "cs504";

    @Autowired
    private CategoryService categoryService;

    public CategoryCrawler() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: CategoryCrawler <proxyFilePath> <logFilePath>");
            return;
        }

        SpringApplication.run(CategoryCrawler.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        initProxyList(strings[0]);
        initLog(strings[1]);
        try {
            startCrawling();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public void startCrawling() throws IOException {
        setProxy();
        Map<String, String> headers = createHeaders();
        Document doc = Jsoup.connect(AMAZON_URL).headers(headers).userAgent(USER_AGENT)
                .timeout(TIMEOUT_IN_MILLISECONDS).get();
        System.out.println(doc.text());
        int i = 2;
        while (true) {
            String selector = CATEGORY_SELECTOR.replace("$NUMBER", String.valueOf(i++));
            Elements results = doc.select(selector);
            if (results.isEmpty()) {
                break;
            }

            String categoryName = results.get(0).text();
            String categorySearchAlias = results.get(0).attr("value");
//            String categoryName = "Alexa Skills";   // for testing
//            String categorySearchAlias = "search-alias=alexa-skills";  // for testing
            System.out.println("category = " + categoryName + ", search-alias = " + categorySearchAlias);
            String productListUrl = PRODUCT_LIST_URL.replace("$SEARCH_ALIAS", categorySearchAlias);
            categoryService.saveCategory(categoryName, productListUrl);

            try {
                Thread.sleep(20000); // wait 2 seconds before next round
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

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

    private void setProxy() {
        do {
            String proxy = proxyList.get(index);
            log.debug("proxy = " + proxy);
            System.setProperty("socksProxyHost", proxy); // set proxy server
            index++;
            if (index == proxyList.size()) {
                index = 0;
            }
        } while (!testProxy());
    }

    private boolean testProxy() {
        Map<String, String> headers = createHeaders();
        try {
            Jsoup.connect(WHAT_IS_MY_IP_ADDRESS).headers(headers).userAgent(USER_AGENT)
                    .timeout(TIMEOUT_IN_MILLISECONDS).get();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void initLog(String logFilePath) {
        try {
            File log = new File(logFilePath);
            if (!log.exists()) {
                log.createNewFile();
            }

            FileWriter fw = new FileWriter(log.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> createHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        return headers;
    }
}
