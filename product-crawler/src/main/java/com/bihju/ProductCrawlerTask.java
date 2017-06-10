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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Log4j
public class ProductCrawlerTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private List<String> proxyList;
    private int index = 0;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private static final String PRODUCT_SELECTOR = "li[data-asin]";
    private static final String[] TITLE_SELECTORS = {
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a > h2"
    };
    private static final String[] PRICE_WHOLE_SELECTORS = {
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div:nth-child(2) > a > span > span > span",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span"
    };
    private static final String[] PRICE_FRACTION_SELECTORS = {
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div:nth-child(2) > a > span > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > sup.sx-price-fractional"
    };
    private static final String[] THUMNAIL_SELECTORS = {
            "#result_$RESULT_NO > div > div.a-row.a-spacing-base > div > a > img",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img"
    };
    private static final String[] DETAIL_URL_SELECTORS = {
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a"
    };
    private static final int TIMEOUT_IN_MILLISECONDS = 100000;
    private final String AUTH_USER = "bittiger";
    private final String AUTH_PASSWORD = "cs504";
    private Map<String, String> headers;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductSource productSource;

    public void init(String proxyFile) {
        initProxyList(proxyFile);
        initHeaders();
    }

    @Scheduled(cron = "0 0 1-22/4 * * *")   // every 4 hours, starting from 1:00 AM
    public void startCrawling() {
        log.info("Start crawling, threadId: " + Thread.currentThread().getId());

        List<Category> categoryList = getAllSubscribedCategories();
        for (Category category : categoryList) {
            String productListUrl = category.getProductListUrl();
            try {
                crawlProduct(category);
            } catch (IOException e) {
                log.warn("Failed to crawl product list url: " + productListUrl);
            }

            delayBetweenCrawling();
        }

        log.info("End cralwing, threadId: " + Thread.currentThread().getId());
    }

    private List<Category> getAllSubscribedCategories() {
        return categoryService.getAllSubscribedCategories();
    }

    // Product URL format:
    // https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias=alexa-skills&field-keywords=-12345&page=$PAGE_NO
    private void crawlProduct(Category category) throws IOException {
        int pageNum = 1;
        boolean isMorePages = true;
        do {
            String productUrl = category.getProductListUrl().replace("$PAGE_NO", String.valueOf(pageNum++));
            log.info("category: " + category.getCategoryName() + ", productUrl = " + productUrl);
            Document doc = getDocument(productUrl);
            if (doc == null) {
                log.warn("Failed to retrieve from productUrl: " + productUrl);
                isMorePages = false;
                return;
            }
            log.info("doc = " + doc.text());
            Elements results = doc.select(PRODUCT_SELECTOR);
            log.info("num of results = " + results.size());

            for (int i = 0; i < results.size(); i++) {
                int index = CrawlerUtil.getResultIndex(results.get(i));
                if (index == -1) {
                    log.info("Cannot get result index for element: " + results.get(i).toString());
                    continue;
                }

                Product product = createProduct(doc, index, category.getId());
                if (product == null) {
                    continue;
                }

                productSource.sendProductToQueue(product);
            }
        } while (isMorePages);
    }

    private Product createProduct(Element doc, int index, long categoryId) throws IOException {
        Product product = new Product();
        if (!updateTitle(product, doc, index)) {
            return null;
        }

        if (!updateThumnail(product, doc, index)) {
            return null;
        }

        if (!updatePrice(product, doc, index)) {
            return null;
        }

        if (!updateDetailUrl(product, doc, index)) {
            return null;
        }

        if (!updateProductId(product, doc, index)) {
            return null;
        }

        product.setCategoryId(categoryId);
        return product;
    }

    private boolean updateTitle(Product product, Element doc, int index) throws IOException {
        for (String titleSelector : TITLE_SELECTORS) {
            String selector = titleSelector.replace("$RESULT_NO", String.valueOf(index));
            Element element = doc.select(selector).first();
            if (element != null) {
                log.info("title = " + element.text());
                product.setTitle(element.text());
                return true;
            }
        }

        log.info("Cannot parse title for product, index = " + index);
        return false;
    }

    private boolean updateThumnail(Product product, Element doc, int index) throws IOException {
        for (String thumnailSelector : THUMNAIL_SELECTORS) {
            String selector = thumnailSelector.replace("$RESULT_NO", String.valueOf(index));
            Element element = doc.select(selector).first();
            if (element != null) {
                log.info("thumnail = " + element.attr("src"));
                product.setThumnail(element.attr("src"));
                return true;
            }
        }

        log.info("Cannot parse thumnail for product, index = " + index);
        return false;
    }

    private boolean updatePrice(Product product, Element doc, int index) throws IOException {
        product.setPrice(0.0);
        boolean isWholePriceValid = false;
        for (String priceWholeSelector : PRICE_WHOLE_SELECTORS) {
            String wholeSelector = priceWholeSelector.replace("$RESULT_NO", String.valueOf(index));
            Element wholeElement = doc.select(wholeSelector).first();
            if (wholeElement != null) {
                log.info("whole price = " + wholeElement.text());
                String wholePrice = wholeElement.text();
                if (wholePrice.contains(",")) {
                    wholePrice = wholePrice.replaceAll(",", "");
                }

                try {
                    product.setPrice(Double.parseDouble(wholePrice));
                    isWholePriceValid = true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    log.info("Cannot parse price whole value for product index: " + index);
                    return false;
                }

                break;
            }
        }

        if (!isWholePriceValid) {
            // Some product is free, it's ok if there is no price set.
            return true;
        }

        for (String priceFractionSelector : PRICE_FRACTION_SELECTORS) {
            String fractionSelector = priceFractionSelector.replace("$RESULT_NO", String.valueOf(index));
            Element fractionElement = doc.select(fractionSelector).first();
            if (fractionElement != null) {
                try {
                    log.info("fraction price = " + fractionElement.text());
                    product.setPrice(product.getPrice() + Double.parseDouble(fractionElement.text()) / 100.0);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    log.info("Cannot parse price fraction value for product index: " + index);
                    return false;
                }

                break;
            }
        }


        // Some product is free, it's ok if there is no price set.
        return true;
    }

    private boolean updateDetailUrl(Product product, Element doc, int index) throws IOException {
        for (String detailUrlSelector : DETAIL_URL_SELECTORS) {
            String selector = detailUrlSelector.replace("$RESULT_NO", String.valueOf(index));
            Element element = doc.select(selector).first();
            if (element != null) {
                String detailUrl = element.attr("href");
                log.debug("detailUrl = " + detailUrl);
                String normalizedUrl = normalizeUrl(detailUrl);
                log.info("normalized detailUrl = " + normalizedUrl);
                product.setDetailUrl(normalizedUrl);
                return true;
            }
        }

        log.info("Cannot parse detailUrl for product, index = " + index);
        return false;
    }

    // Remove ref part of url
    private String normalizeUrl(String url) {
        if (url == null) {
            return url;
        }

        int i = url.indexOf("ref");
        String normalizedUrl = i == -1 ? url : url.substring(0, i - 1);

        if (normalizedUrl == null || normalizedUrl.trim().isEmpty()) {
            log.info("Empty url: " + url);
        }

        return normalizedUrl;
    }

    private boolean updateProductId(Product product, Element doc, int index) {
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

    private Document getDocument(String productUrl) {
        Document document = null;
        int retryCount = proxyList.size();

        do {
            retryCount--;
            String proxy = proxyList.get(index);
            log.info("proxy = " + proxy);
            System.setProperty("socksProxyHost", proxy); // set proxy server
            index++;
            if (index == proxyList.size()) {
                index = 0;
            }
            try {
                document = Jsoup.connect(productUrl).headers(headers).userAgent(USER_AGENT)
                        .timeout(TIMEOUT_IN_MILLISECONDS).maxBodySize(0).get();
            } catch (IOException | IllegalArgumentException e) {
                log.info("textProxy() failed: index = " + index);
            }
        } while (document == null && retryCount > 0);

        return document;
    }

    private void initHeaders() {
        headers = new HashMap<>();
        headers.put("Accept", "text/html,text/plain");
        headers.put("Accept-Language", "en-us,en");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Accept-Charset", "utf-8");
    }
}
