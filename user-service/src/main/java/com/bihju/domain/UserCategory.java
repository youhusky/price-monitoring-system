package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@Table(name = "user_category")
public class UserCategory {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long categoryId;

    @JsonCreator
    public UserCategory(@JsonProperty("userId") Long userId, @JsonProperty("categoryId") Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }
}
