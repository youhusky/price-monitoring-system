package com.bihju.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "product")
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
    private long createTime;
    private long updateTime;
}
