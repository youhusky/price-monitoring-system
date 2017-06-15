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

    @JsonCreator
    public CategoryPriority(@JsonProperty("category_id") long categoryId,
                            @JsonProperty("priority") int priority,
                            @JsonProperty("user_count") long userCount) {
        this.categoryId = categoryId;
        this.priority = priority;
        this.userCount = userCount;
    }
}
