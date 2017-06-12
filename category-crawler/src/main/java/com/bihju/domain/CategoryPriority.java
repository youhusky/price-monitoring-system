package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "category_priority", indexes = {@Index(name = "index_category_priority",
        columnList = "categoryId, priority", unique = true)})
public class CategoryPriority {
    @Id
    @GeneratedValue
    private long id;

    private long categoryId;
    private int priority;
    private long userCount;
    private long createTime;
    private long updateTime;

    public CategoryPriority() {
    }

    @JsonCreator
    public CategoryPriority(@JsonProperty("category_id") long categoryId,
                            @JsonProperty("priority") int priority,
                            @JsonProperty("user_count") long userCount) {
        this.categoryId = categoryId;
        this.priority = priority;
        this.userCount = userCount;
    }
}
