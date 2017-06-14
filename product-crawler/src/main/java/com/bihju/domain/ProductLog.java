package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class ProductLog {
    public enum Status {
        SUCCESS, FAIL
    }

    @Id
    @GeneratedValue
    private Long id;

    private Status status = Status.SUCCESS;
    private String message;
    private long threadId;
    private String productUrl;
    private String categoryName;
    private int pageNumber;
    private long timestampInMilli;

    @JsonCreator
    public ProductLog(@JsonProperty("status") Status status, @JsonProperty("category_name") String categoryName,
                      @JsonProperty("product_url") String productUrl, @JsonProperty("page_number") int pageNumber,
                      @JsonProperty("message") String message) {
        this.status = status;
        this.message = message;
        this.categoryName = categoryName;
        this.productUrl = productUrl;
        this.pageNumber = pageNumber;
        this.threadId = Thread.currentThread().getId();
        this.timestampInMilli = System.currentTimeMillis();
    }

}
