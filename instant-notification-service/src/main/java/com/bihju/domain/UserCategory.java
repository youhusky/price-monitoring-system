package com.bihju.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "user_category")
public class UserCategory {
    @Id
    @GeneratedValue
    private long id;

    private long userId;
    private long categoryId;
    private long createTime;
    private long updateTime;
}
