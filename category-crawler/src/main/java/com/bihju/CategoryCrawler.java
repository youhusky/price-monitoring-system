package com.bihju;

import lombok.extern.log4j.Log4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class CategoryCrawler {
    private List<String> proxyList;
    private int index = 0;
    BufferedWriter logBFWriter;
    private static final String AMAZON_URL = "https://www.amazon.com";
    private static final String WHAT_IS_MY_IP_ADDRESS = "https://whatismyipaddress.com";
//    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";

    private static final String CATEGORY_SELECTOR = "#searchDropdownBox > option:nth-child($NUMBER)";
    private static final int TIMEOUT_IN_MILLISECONDS = 100000;
    private final String AUTH_USER = "bittiger";
    private final String AUTH_PASSWORD = "cs504";

    public CategoryCrawler(String proxyFilePath, String logFilePath) {
        initProxyList(proxyFilePath);
        initLog(logFilePath);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: CategoryCrawler <proxyFilePath> <logFilePath>");
            return;
        }

        CategoryCrawler categoryCrawler = new CategoryCrawler(args[0], args[1]);
        try {
            categoryCrawler.startCrawling();
        } catch (IOException e) {
            log.debug(e.getMessage());
        } finally {
            categoryCrawler.cleanup();
        }
    }

    public void startCrawling() throws IOException {
        setProxy();
        Map<String, String> headers = createHeaders();
        Connection connect = Jsoup.connect(AMAZON_URL);
        Document doc = null;
        if (connect != null) {
            connect.headers(headers).userAgent(USER_AGENT).timeout(TIMEOUT_IN_MILLISECONDS);
            doc = connect.get();
        }

//        Document doc = Jsoup.connect(AMAZON_URL).headers(headers).userAgent(USER_AGENT)
//                .timeout(TIMEOUT_IN_MILLISECONDS).get();
//        System.out.println(doc.text());
        int i = 2;
        while (true) {
            String selector = CATEGORY_SELECTOR.replace("$NUMBER", String.valueOf(i++));
            Elements results = doc.select(selector);
            if (results == null) {
                break;
            }

            System.out.println("result = " + results.text());

        }

//        for (int i = 0; i < results.size(); i++) {
//            int index = CrawlerUtil.getResultIndex(results.get(i));
//            if (index == -1) {
//                logBFWriter.write("cannot get result index for element of query: " + query + ", element: " + results.get(i).toString());
//                logBFWriter.newLine();
//                continue;
//            }
//            Ad ad = createAd(doc, index, logBFWriter, query, bidPrice, campaignId, queryGroupId);
//            if (ad == null || (category != null && ad.category != category)) {
//                continue;
//            }
//            products.add(ad);
//        }
    }

    public void cleanup() {
        if (logBFWriter != null) {
            try {
                logBFWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initProxyList(String proxy_file) {
        proxyList = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(proxy_file))) {
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
            Document doc = Jsoup.connect(WHAT_IS_MY_IP_ADDRESS).headers(headers).userAgent(USER_AGENT)
                    .timeout(TIMEOUT_IN_MILLISECONDS).get();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void initLog(String log_path) {
        try {
            File log = new File(log_path);
            if (!log.exists()) {
                log.createNewFile();
            }

            FileWriter fw = new FileWriter(log.getAbsoluteFile());
            logBFWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> createHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html, application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        return headers;
    }
}
