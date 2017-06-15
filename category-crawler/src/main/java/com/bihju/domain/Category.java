package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@Table(name = "category", indexes = {@Index(name = "index_category_name", columnList = "categoryName", unique = true)})
public class Category {
    @Id
    @GeneratedValue
    private long id;

    private String categoryName;
    private String productListUrl;
    private long createTime;
    private long updateTime;

    @JsonCreator
    public Category(@JsonProperty("category_name") String categoryName,
                    @JsonProperty("product_list_url") String productListUrl) {
        this.categoryName = categoryName;
        this.productListUrl = productListUrl;
    }
}
