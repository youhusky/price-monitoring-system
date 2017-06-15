package com.bihju.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@Table(name = "user_category", indexes = {@Index(name = "index_user_id_category_id", columnList = "userId, categoryId", unique = true)})
public class UserCategory {
    @Id
    @GeneratedValue
    private long id;

    private long userId;
    private long categoryId;
    private long createTime;
    private long updateTime;
}
