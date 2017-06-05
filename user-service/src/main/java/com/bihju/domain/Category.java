package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue
    private Long id;

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
