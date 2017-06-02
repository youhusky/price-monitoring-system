package com.bihju;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.service.CategoryService;
import com.bihju.util.CrawlerUtil;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@EnableBinding(Source.class)
@SpringBootApplication
public class ProductCrawler implements CommandLineRunner {
    private List<String> proxyList;
    private int index = 0;
    private static final String WHAT_IS_MY_IP_ADDRESS = "https://whatismyipaddress.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private static final String PRODUCT_SELECTOR = "li[data-asin]";
    private static final String TITLE_SELECTOR = "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a";
    private static final String PRICE_WHOLE_SELECTOR = "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span";
    private static final String PRICE_FRACTION_SELECTOR = "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > sup.sx-price-fractional";
    private static final String THUMNAIL_SELECTOR = "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img";
    private static final String DETAIL_URL_SELECTOR = "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a";
    private static final int TIMEOUT_IN_MILLISECONDS = 100000;
    private final String AUTH_USER = "bittiger";
    private final String AUTH_PASSWORD = "cs504";
    private List<Category> categoryList;
    private Map<String, String> headers;
    private BufferedWriter logBufferedWriter;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MessageChannel output;

    public ProductCrawler() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            log.error("Usage: ProductCrawler <proxyFilePath> <logFilePath>");
            return;
        }

        SpringApplication.run(ProductCrawler.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        initProxyList(strings[0]);
        initLog(strings[1]);
        initCategoryProductListUrl();
        try {
//            startCrawling();
            testQueue();
//        } catch (IOException e) {
//            log.error(e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void testQueue() {
        Product product = new Product();
        product.setProductId("ABCDEFG");
        product.setDetailUrl("https://www.yahoo.com");
        product.setThumnail("https://www.yahoo.com");
        product.setTitle("Test for products queue");
        product.setCategoryId(12345);
        product.setPrice(0.0);
        sendProductToQueue(product);
    }

    private void initCategoryProductListUrl() {
        categoryList = categoryService.getAllCategories();
    }

    public void startCrawling() throws IOException {
        setProxy();
        headers = createHeaders();
        for (Category category : categoryList) {
            String productListUrl = category.getProductListUrl();
            try {
                crawlProduct(category);
            } catch (IOException e) {
                log.warn("Failed to crawl product list url: " + productListUrl);
                continue;
            }

            delayBetweenCrawling();
        }
    }

    // Product URL format:
    // https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias=alexa-skills&field-keywords=-12345&page=$PAGE_NO
    private void crawlProduct(Category category) throws IOException {
        int pageNum = 1;
        String productUrl = category.getProductListUrl().replace("$PAGE_NO", String.valueOf(pageNum++));
        log.debug("category: " + category.getCategoryName() + ", productUrl = " + productUrl);
        Document doc = Jsoup.connect(productUrl).headers(headers).userAgent(USER_AGENT)
                .timeout(TIMEOUT_IN_MILLISECONDS).get();
        System.out.println(doc.text());
        Elements results = doc.select(PRODUCT_SELECTOR);
        if (results.isEmpty()) {
            log.debug("All products are retrieved for category: " + category.getCategoryName());
            return;
        }

        for (int i = 0; i < results.size(); i++) {
            int index = CrawlerUtil.getResultIndex(results.get(i));
            if (index == -1) {
                logBufferedWriter.write("Cannot get result index for element: " + results.get(i).toString());
                logBufferedWriter.newLine();
                continue;
            }

            Product product = createProduct(doc, index, category.getId(), logBufferedWriter);
            if (product == null) {
                continue;
            }

            sendProductToQueue(product);
        }
    }

    private void sendProductToQueue(Product product) {
        output.send(MessageBuilder.withPayload(product).build());
    }

    private Product createProduct(Element doc, int index, long categoryId, BufferedWriter logBufferedWriter) throws IOException {
        Product product = new Product();
        if (!updateTitle(product, doc, index, logBufferedWriter)) {
            return null;
        }

        if (!updateThumnail(product, doc, index, logBufferedWriter)) {
            return null;
        }

        if (!updatePrice(product, doc, index, logBufferedWriter)) {
            return null;
        }

        if (!updateDetailUrl(product, doc, index, logBufferedWriter)) {
            return null;
        }

        if (!updateProductId(product, doc, index, logBufferedWriter)) {
            return null;
        }

        product.setCategoryId(categoryId);
        return product;
    }

    private boolean updateTitle(Product product, Element doc, int index, BufferedWriter logBufferedWriter) throws IOException {
        String selector = TITLE_SELECTOR.replace("$RESULT_NO", String.valueOf(index));
        Element element = doc.select(selector).first();
        if (element != null) {
            log.info("title = " + element.text());
            product.setTitle(element.text());
        } else {
            logBufferedWriter.write("Cannot parse title for product, index = " + index);
            logBufferedWriter.newLine();
            return false;
        }

        return true;
    }

    private boolean updateThumnail(Product product, Element doc, int index, BufferedWriter logBufferedWriter) throws IOException {
        String selector = THUMNAIL_SELECTOR.replace("$RESULT_NO", String.valueOf(index));
        Element element = doc.select(selector).first();
        if (element != null) {
            log.info("thumnail = " + element.attr("src"));
            product.setThumnail(element.attr("src"));
        } else {
            logBufferedWriter.write("Cannot parse thumnail for product, index = " + index);
            logBufferedWriter.newLine();
            return false;
        }

        return true;
    }

    private boolean updatePrice(Product product, Element doc, int index, BufferedWriter logBufferedWriter) throws IOException {
        String wholeSelector = PRICE_WHOLE_SELECTOR.replace("$RESULT_NO", String.valueOf(index));
        String fractionSelector = PRICE_FRACTION_SELECTOR.replace("$RESULT_NO", String.valueOf(index));
        product.setPrice(0.0);
        Element wholeElement = doc.select(wholeSelector).first();
        if (wholeElement != null) {
            log.info("whole price = " + wholeElement.text());
            String wholePrice = wholeElement.text();
            if (wholePrice.contains(",")) {
                wholePrice = wholePrice.replaceAll(",", "");
            }

            try {
                product.setPrice(Double.parseDouble(wholePrice));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                logBufferedWriter.write("Cannot parse price whole value for product index: " + index);
                logBufferedWriter.newLine();
            }
        }

        Element fractionElement = doc.select(fractionSelector).first();
        if (fractionElement != null) {
            try {
                log.info("fraction price = " + fractionElement.text());
                product.setPrice(product.getPrice() + Double.parseDouble(fractionElement.text()) / 100.0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                logBufferedWriter.write("Cannot parse price fraction value for product index: " + index);
                logBufferedWriter.newLine();
            }
        }

        // Some product is free, it's ok if there is no price set.
        return true;
    }

    private boolean updateDetailUrl(Product product, Element doc, int index, BufferedWriter logBufferedWriter) throws IOException {
        String selector = DETAIL_URL_SELECTOR.replace("$RESULT_NO", String.valueOf(index));
        Element element = doc.select(selector).first();
        if (element != null) {
            String detailUrl = element.attr("href");
            System.out.println("detailUrl = " + detailUrl);
            String normalizedUrl = normalizeUrl(detailUrl);
            log.info("detailUrl = " + normalizedUrl);
            product.setDetailUrl(normalizedUrl);
        } else {
            logBufferedWriter.write("Cannot parse detailUrl for product, index = " + index);
            logBufferedWriter.newLine();
            return false;
        }

        return true;
    }

    // Remove ref part of url
    private String normalizeUrl(String url) {
        if (url == null) {
            return url;
        }

        int i = url.indexOf("ref");
        String normalizedUrl = i == -1 ? url : url.substring(0, i - 1);

        if (normalizedUrl == null || normalizedUrl.trim().isEmpty()) {
            log.debug("Empty url: " + url);
        }

        return normalizedUrl;
    }

    private boolean updateProductId(Product product, Element doc, int index, BufferedWriter logBufferedWriter) {
        String detailUrl = product.getDetailUrl();
        int productIdIndex = detailUrl.lastIndexOf('/');
        if (productIdIndex == -1) {
            return false;
        }

        String productId = detailUrl.substring(productIdIndex + 1);
        product.setProductId(productId);
        return true;
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
            logBufferedWriter = new BufferedWriter(fw);
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

    public void cleanup() {
        if (logBufferedWriter != null) {
            try {
                logBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
