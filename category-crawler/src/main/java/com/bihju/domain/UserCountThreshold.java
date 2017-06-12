package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_count_threshold")
public class UserCountThreshold {
    @Id
    @GeneratedValue
    private long id;

    private long highPriorityUserCount;
    private long createTime;
    private long updateTime;

    public UserCountThreshold() {
    }

    @JsonCreator
    public UserCountThreshold(@JsonProperty("high_priority_user_count") long highPriorityUserCount) {
        this.highPriorityUserCount = highPriorityUserCount;
    }
}
