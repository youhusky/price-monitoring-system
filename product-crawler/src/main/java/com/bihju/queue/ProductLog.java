package com.bihju.queue;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductLog {
    public static enum STATUS {
        SUCCESS, FAIL
    }

    private STATUS status;
    private String message;
    private long threadId;
    private String productUrl;
    private String categoryName;
    private int pageNumber;
    private long timestampInMilli;

    public ProductLog(STATUS status, String categoryName, String productUrl, int pageNumber, String message) {
        this.status = status;
        this.message = message;
        this.categoryName = categoryName;
        this.productUrl = productUrl;
        this.pageNumber = pageNumber;
        this.threadId = Thread.currentThread().getId();
        this.timestampInMilli = System.currentTimeMillis();
    }

}
