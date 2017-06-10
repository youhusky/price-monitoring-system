package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "category", indexes = {@Index(name = "index_category_name", columnList = "categoryName", unique = true)})
public class Category {
    @Id
    @GeneratedValue
    private long id;

    private String categoryName;
    private String productListUrl;

    public Category() {
        this.categoryName = null;
        this.productListUrl = null;
    }

    @JsonCreator
    public Category(@JsonProperty("category-name") String categoryName,
                    @JsonProperty("product-list-url") String productListUrl) {
        this.categoryName = categoryName;
        this.productListUrl = productListUrl;
    }
}
