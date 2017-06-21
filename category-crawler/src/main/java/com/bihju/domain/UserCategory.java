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
    private double minDiscountPercent;
    private long createTime;
    private long updateTime;

    @JsonCreator
    public UserCategory(@JsonProperty("userId") Long userId, @JsonProperty("categoryId") Long categoryId,
                        @JsonProperty("minDiscountPercent") Double minDiscountPercent) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.minDiscountPercent = minDiscountPercent;
    }
}
