package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
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
@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
@Table(name = "user_count_threshold")
public class UserCountThreshold {
    @Id
    @GeneratedValue
    private long id;

    private long highPriorityUserCount;
    private long createTime;
    private long updateTime;

    @JsonCreator
    public UserCountThreshold(@JsonProperty("high_priority_user_count") long highPriorityUserCount) {
        this.highPriorityUserCount = highPriorityUserCount;
    }
}
