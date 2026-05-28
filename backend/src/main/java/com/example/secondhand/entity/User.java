package com.example.secondhand.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user", indexes = {
    @Index(name = "uk_username", columnList = "username", unique = true)
})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String avatar;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer role = 0;

    @Column(name = "audit_status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer auditStatus = 0;

    @Column(name = "balance", columnDefinition = "DECIMAL(12,2) DEFAULT 0")
    private Double balance = 0.0;

    @Column(name = "frozen_balance", columnDefinition = "DECIMAL(12,2) DEFAULT 0")
    private Double frozenBalance = 0.0;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
