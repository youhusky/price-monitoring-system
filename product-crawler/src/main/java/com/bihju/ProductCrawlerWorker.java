package com.bihju;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.util.CrawlerUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Log4j
public class ProductCrawlerWorker implements Runnable {
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
    private Map<String, String> headers;
    private Category category;
    private List<String> proxyList;
    private AtomicInteger proxyIndex;
    private ProductSource productSource;

    @Autowired
    public ProductCrawlerWorker(Category category, List<String> proxyList, AtomicInteger proxyIndex, Map<String,
            String> headers, ProductSource productSource) {
        this.category = category;
        this.proxyList = proxyList;
        this. proxyIndex = proxyIndex;
        this.headers = headers;
        this.productSource = productSource;
    }

    @Override
    public void run() {
        int pageNum = 1;
        boolean isMorePages = true;
        do {
            String productListUrl = category.getProductListUrl().replace("$PAGE_NO", String.valueOf(pageNum++));
            log.info("category: " + category.getCategoryName() + ", productUrl = " + productListUrl + ", threadId = " + Thread.currentThread().getId());
            Document doc = getDocument(productListUrl, proxyList);
            if (doc == null) {
                log.warn("Failed to retrieve from productUrl: " + productListUrl);
                isMorePages = false;
                return;
            }
            log.info("doc = " + doc.text());
            Elements results = doc.select(PRODUCT_SELECTOR);
            log.info("num of results = " + results.size());

            for (int i = 0; i < results.size(); i++) {
                int index = CrawlerUtil.getResultIndex(results.get(i));
                if (index == -1) {
                    log.info("Cannot get result index for element: " + results.get(i).toString() + ", threadId = " + Thread.currentThread().getId());
                    continue;
                }

                try {
                    Product product = createProduct(doc, index, category.getId());
                    if (product == null) {
                        continue;
                    }
                    productSource.sendProductToQueue(product);
                } catch (IOException e) {
                    log.warn("Failed to crawl product list url: " + productListUrl + ", threadId = " + Thread.currentThread().getId());
                }

            }

            delayBetweenCrawling();
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

        log.info("Cannot parse title for product, index = " + index + ", threadId = " + Thread.currentThread().getId());
        return false;
    }

    private boolean updateThumnail(Product product, Element doc, int index) throws IOException {
        for (String thumnailSelector : THUMNAIL_SELECTORS) {
            String selector = thumnailSelector.replace("$RESULT_NO", String.valueOf(index));
            Element element = doc.select(selector).first();
            if (element != null) {
                log.info("thumnail = " + element.attr("src") + ", threadId = " + Thread.currentThread().getId());
                product.setThumnail(element.attr("src"));
                return true;
            }
        }

        log.info("Cannot parse thumnail for product, index = " + index + ", threadId = " + Thread.currentThread().getId());
        return false;
    }

    private boolean updatePrice(Product product, Element doc, int index) throws IOException {
        product.setPrice(0.0);
        boolean isWholePriceValid = false;
        for (String priceWholeSelector : PRICE_WHOLE_SELECTORS) {
            String wholeSelector = priceWholeSelector.replace("$RESULT_NO", String.valueOf(index));
            Element wholeElement = doc.select(wholeSelector).first();
            if (wholeElement != null) {
                log.info("whole price = " + wholeElement.text() + ", threadId = " + Thread.currentThread().getId());
                String wholePrice = wholeElement.text();
                if (wholePrice.contains(",")) {
                    wholePrice = wholePrice.replaceAll(",", "");
                }

                try {
                    product.setPrice(Double.parseDouble(wholePrice));
                    isWholePriceValid = true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    log.info("Cannot parse price whole value for product index: " + index + ", threadId = " + Thread.currentThread().getId());
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
                    log.info("fraction price = " + fractionElement.text() + ", threadId = " + Thread.currentThread().getId());
                    product.setPrice(product.getPrice() + Double.parseDouble(fractionElement.text()) / 100.0);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    log.info("Cannot parse price fraction value for product index: " + index + ", threadId = " + Thread.currentThread().getId());
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
                log.debug("detailUrl = " + detailUrl + ", threadId = " + Thread.currentThread().getId());
                String normalizedUrl = normalizeUrl(detailUrl);
                log.info("normalized detailUrl = " + normalizedUrl + ", threadId = " + Thread.currentThread().getId());
                product.setDetailUrl(normalizedUrl);
                return true;
            }
        }

        log.info("Cannot parse detailUrl for product, index = " + index + ", threadId = " + Thread.currentThread().getId());
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
            log.info("Empty url: " + url + ", threadId = " + Thread.currentThread().getId());
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

    private Document getDocument(String productUrl, List<String> proxyList) {
        Document document = null;
        int retryCount = proxyList.size();

        do {
            retryCount--;
            String proxy = proxyList.get(proxyIndex.get());
            log.info("proxy = " + proxy + ", threadId = " + Thread.currentThread().getId());
            System.setProperty("socksProxyHost", proxy); // set proxy server
            if (proxyIndex.get() == proxyList.size() - 1) {
                proxyIndex.set(0);
            } else {
                proxyIndex.incrementAndGet();
            }
            try {
                document = Jsoup.connect(productUrl).headers(headers).userAgent(USER_AGENT)
                        .timeout(TIMEOUT_IN_MILLISECONDS).maxBodySize(0).get();
            } catch (IOException | IllegalArgumentException e) {
                log.info("Test proxy failed: proxyIndex = " + proxyIndex.get() + ", threadId = " + Thread.currentThread().getId());
            }
        } while (document == null && retryCount > 0);

        return document;
    }
}
