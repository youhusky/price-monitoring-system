package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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

    @JsonCreator
    public UserCategory(@JsonProperty("user_id") Long userId, @JsonProperty("category_id") Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }
}
