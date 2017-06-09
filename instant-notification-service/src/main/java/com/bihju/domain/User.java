package com.bihju.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "user")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {
    public enum NotificationType {
        INSTANT, DAILY;
    }

    @Id
    @GeneratedValue
    private long id;

    private String email;
    private String password;
    private NotificationType notificationType = NotificationType.INSTANT;
    private long createTime;
    private long updateTime;
}
