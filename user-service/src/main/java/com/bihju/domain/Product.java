package com.bihju.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@Table(name = "product", indexes = {@Index(name = "index_product_id", columnList = "productId", unique = true)})
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    private String productId;
    private long categoryId;
    private String title;
    private String thumnail;
    private String detailUrl;
    private double price;
    private double oldPrice;
    private double discountPercent;
    private long createTime;
    private long updateTime;
}
