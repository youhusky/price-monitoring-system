package com.bihju;

import com.bihju.domain.Category;
import com.bihju.domain.Product;
import com.bihju.domain.ProductLog;
import com.bihju.queue.ProductSource;
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
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div.a-row.a-spacing-none.scx-truncate-medium.sx-line-clamp-2 > a > h2",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a > h2",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div > a > h2",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-small.a-grid-vertical-align.a-grid-center > div > a > span",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none.s-color-subdued > div.a-row.a-spacing-micro > a > h2",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-small.a-grid-vertical-align.a-grid-center > div > a > img",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a > h2",
            "#result_$RESULT_NO > div > div:nth-child(3) > div:nth-child(1) > a > h2",
            "#result_$RESULT_NO > div > div:nth-child(3) > div:nth-child(1) > a"
    };
    private static final String[] FULL_PRICE_SELECTORS = {
            "#result_$RESULT_NO > div > div:nth-child(4) > div > a > span.a-size-base.a-color-base"
    };
    private static final String[] PRICE_WHOLE_SELECTORS = {
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > table > tbody > tr.a-spacing-none.s-table-twister-row-no-border.s-table-twister-row > td:nth-child(2) > div > a > span > span > span",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(2) > a > span > span > span:nth-child(2)",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(2) > a > span > span > span",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none.s-color-subdued > div:nth-child(2) > a > span > span > span > span",
            "#result_$RESULT_NO > div > div:nth-child(4) > a > span.a-color-base.sx-zero-spacing > span > span",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div:nth-child(2) > a > span > span > span"
    };
    private static final String[] PRICE_FRACTION_SELECTORS = {
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > table > tbody > tr.a-spacing-none.s-table-twister-row-no-border.s-table-twister-row > td:nth-child(2) > div > a > span > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(2) > a > span > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div:nth-child(2) > a > span > span > sup:nth-child(3)",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none.s-color-subdued > div:nth-child(2) > a > span > span > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div:nth-child(4) > a > span.a-color-base.sx-zero-spacing > span > sup.sx-price-fractional",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div:nth-child(2) > a > span > span > sup.sx-price-fractional"
    };
    private static final String[] THUMNAIL_SELECTORS = {
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img",
            "#rot-$PRODUCT_ID > div > a > div.s-card.s-card-group-rot-$PRODUCT_ID.s-active > img",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-base > div > div > a > img",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-base > div > a > img"
    };
    private static final String[] DETAIL_URL_SELECTORS = {
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div.a-row.a-spacing-none.scx-truncate-medium.sx-line-clamp-2 > a",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a",
            "#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div > a",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none.s-color-subdued > div.a-row.a-spacing-micro > a",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-small.a-grid-vertical-align.a-grid-center > div > a",
            "#result_$RESULT_NO > div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a",
            "#result_$RESULT_NO > div > div:nth-child(3) > div:nth-child(1) > a"
    };
    private static final int TIMEOUT_IN_MILLISECONDS = 100000;

    private Map<String, String> headers;
    private Category category;
    private List<String> proxyList;
    private AtomicInteger proxyIndex;
    private ProductSource productSource;
    private int priority;

    @Autowired
    public ProductCrawlerWorker(Category category, List<String> proxyList, AtomicInteger proxyIndex, Map<String,
            String> headers, ProductSource productSource, int priority) {
        this.category = category;
        this.proxyList = proxyList;
        this. proxyIndex = proxyIndex;
        this.headers = headers;
        this.productSource = productSource;
        this.priority = priority;
    }

    @Override
    public void run() {
        int pageNum = 1;
        String categoryName = category.getCategoryName();
        boolean isMorePages = true;
        do {
            String productListUrl = category.getProductListUrl().replace("$PAGE_NO", String.valueOf(pageNum++));
            productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName,
                    productListUrl, pageNum, "Successful get product url."));
            Document doc = getDocument(productListUrl, proxyList, categoryName, productListUrl, pageNum);
            if (doc == null) {
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                "Failed to retrieve document from productUrl"));
                isMorePages = false;
                return;
            }
            Elements results = doc.select(PRODUCT_SELECTOR);
            productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                    "Successfully retrieved list, num of results = " + results.size()));

            for (int i = 0; i < results.size(); i++) {
                int index = CrawlerUtil.getResultIndex(results.get(i));
                if (index == -1) {
                    productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                        "Cannot get result index for element: " + results.get(i).toString()));
                    continue;
                }

                try {
                    Product product = createProduct(doc, index, category.getId(), categoryName, productListUrl, pageNum);
                    if (product == null) {
                        continue;
                    }
                    productSource.sendProductToQueue(product, priority);
                } catch (IOException e) {
                    productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                    "Failed to crawl product list url: " + productListUrl));
                }

            }

            delayBetweenCrawling();
        } while (isMorePages);
    }
    private Product createProduct(Element doc, int index, long categoryId, String categoryName, String productListUrl,
                                  int pageNum) throws IOException {
        Product product = new Product();
        if (!updateTitle(product, doc, index, categoryName, productListUrl, pageNum)) {
            return null;
        }

        if (!updatePrice(product, doc, index, categoryName, productListUrl, pageNum)) {
            return null;
        }

        if (!updateDetailUrl(product, doc, index, categoryName, productListUrl, pageNum)) {
            return null;
        }

        if (!updateProductId(product, doc, index, categoryName, productListUrl, pageNum)) {
            return null;
        }

        if (!updateThumnail(product, doc, index, categoryName, productListUrl, pageNum)) {
            return null;
        }

        product.setCategoryId(categoryId);
        return product;
    }

    private boolean updateTitle(Product product, Element doc, int index, String categoryName, String productListUrl, int pageNum) throws IOException {
        for (String titleSelector : TITLE_SELECTORS) {
            String selector = titleSelector.replace("$RESULT_NO", String.valueOf(index));
            Element element = doc.select(selector).first();
            if (element != null) {
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                "title = " + element.text() + ", index = " + index));
                product.setTitle(element.text());
                return true;
            }
        }

        productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                "Cannot parse title for product, index = " + index));
        return false;
    }

    private boolean updateThumnail(Product product, Element doc, int index, String categoryName, String productListUrl, int pageNum) throws IOException {
        for (String thumnailSelector : THUMNAIL_SELECTORS) {
            String selector = thumnailSelector.replace("$RESULT_NO", String.valueOf(index))
                    .replace("$PRODUCT_ID", product.getProductId());
            Element element = doc.select(selector).first();
            if (element != null) {
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                        "thumnail = " + element.attr("src") + ", index = " + index));
                product.setThumnail(element.attr("src"));
                return true;
            }
        }

        productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                "Cannot parse thumnail for product, index = " + index));
        return false;
    }

    private boolean updatePrice(Product product, Element doc, int index, String categoryName, String productListUrl, int pageNum) throws IOException {
        product.setPrice(0.0);
        boolean isWholePriceValid = false;
        for (String priceWholeSelector : PRICE_WHOLE_SELECTORS) {
            String wholeSelector = priceWholeSelector.replace("$RESULT_NO", String.valueOf(index));
            Element wholeElement = doc.select(wholeSelector).first();
            if (wholeElement != null) {
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                "whole price = " + wholeElement.text() + ", index = " + index));
                String wholePrice = wholeElement.text();
                if (wholePrice.contains(",")) {
                    wholePrice = wholePrice.replaceAll(",", "");
                }

                try {
                    product.setPrice(Double.parseDouble(wholePrice));
                    isWholePriceValid = true;
                } catch (NumberFormatException e) {
                    productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                    "Cannot parse price whole value for product index: " + index));
                    continue;
                }

                if (isWholePriceValid) {
                    break;
                }
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
                    productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                    "fraction price = " + fractionElement.text() + ", index = " + index));
                    product.setPrice(product.getPrice() + Double.parseDouble(fractionElement.text()) / 100.0);
                } catch (NumberFormatException e) {
                    productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                    "Cannot parse price fraction value for product index: " + index));
                    continue;
                }

                if (product.getPrice() != 0.0) {
                    break;
                }
            }
        }

        if (product.getPrice() == 0.0) {
            for (String fullPriceSelector : FULL_PRICE_SELECTORS) {
                String fullSelector = fullPriceSelector.replace("$RESULT_NO", String.valueOf(index));
                Element fullElement = doc.select(fullSelector).first();
                if (fullElement != null) {
                    productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                            "full price = " + fullElement.text() + ", index = " + index));
                    String fullPrice = fullElement.text();
                    if (fullPrice.contains(",")) {
                        fullPrice = fullPrice.replaceAll(",", "");
                    }

                    try {
                        product.setPrice(Double.parseDouble(fullPrice));
                    } catch (NumberFormatException e) {
                        productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                                "Cannot parse price full value for product index: " + index));
                        continue;
                    }

                    if (product.getPrice() != 0.0) {
                        break;
                    }
                }
            }
        }

        // Some product is free, it's ok if there is no price set.
        return true;
    }

    private boolean updateDetailUrl(Product product, Element doc, int index, String categoryName, String productListUrl, int pageNum) throws IOException {
        for (String detailUrlSelector : DETAIL_URL_SELECTORS) {
            String selector = detailUrlSelector.replace("$RESULT_NO", String.valueOf(index));
            Element element = doc.select(selector).first();
            if (element != null) {
                String detailUrl = element.attr("href");
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                "detailUrl = " + detailUrl + ", index = " + index));
                String normalizedUrl = normalizeUrl(detailUrl, categoryName, productListUrl, pageNum);
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
                "normalized detailUrl = " + normalizedUrl + ", index = " + index));
                product.setDetailUrl(normalizedUrl);
                return true;
            }
        }

        productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productListUrl, pageNum,
        "Cannot parse detailUrl for product, index = " + index));
        return false;
    }

    // Remove ref part of url
    private String normalizeUrl(String url, String categoryName, String productListUrl, int pageNum) {
        if (url == null) {
            return url;
        }

        int i = url.indexOf("ref");
        String normalizedUrl = i == -1 ? url : url.substring(0, i - 1);

        if (normalizedUrl == null || normalizedUrl.trim().isEmpty()) {
            productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                    "Empty url: " + url));
        }

        return normalizedUrl;
    }

    private boolean updateProductId(Product product, Element doc, int index, String categoryName, String productListUrl, int pageNum) {
        String detailUrl = product.getDetailUrl();
        int productIdIndex = detailUrl.lastIndexOf('/');
        if (productIdIndex == -1) {
            productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productListUrl, pageNum,
                    "Failed to get productId, index: " + index));
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
            // Ignore
        }
    }

    private Document getDocument(String productUrl, List<String> proxyList, String categoryName, String productListUrl, int pageNum) {
        Document document = null;
        int retryCount = proxyList.size();

        do {
            retryCount--;
            String proxy = proxyList.get(proxyIndex.get());
            productSource.sendLogToQueue(new ProductLog(ProductLog.Status.SUCCESS, categoryName, productUrl, pageNum,
                    "proxy = " + proxy));
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
                productSource.sendLogToQueue(new ProductLog(ProductLog.Status.FAIL, categoryName, productUrl, pageNum,
                "Test proxy failed: proxyIndex = " + proxyIndex.get()));
            }
        } while (document == null && retryCount > 0);

        return document;
    }
}
