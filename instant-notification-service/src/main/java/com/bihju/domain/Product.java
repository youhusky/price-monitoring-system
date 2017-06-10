package com.bihju.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "product", indexes = {@Index(name = "index_product_id", columnList = "productId", unique = true)})
public class Product {
    @Id
    @GeneratedValue
    private long id;

    private String productId;
    private long categoryId;
    private String title;
    private String thumnail;
    private String detailUrl;
    private double price;
    private double oldPrice;
    private long createTime;
    private long updateTime;
}
