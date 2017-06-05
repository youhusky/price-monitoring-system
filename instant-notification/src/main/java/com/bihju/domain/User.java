package com.bihju.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "user")
public class User {
    public enum NotificationType {
        INSTANT, DAILY;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String password;
    private NotificationType notificationType;
    private long lastUpdatedTime;
}
