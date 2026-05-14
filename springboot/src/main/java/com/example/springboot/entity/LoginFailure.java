package com.example.springboot.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "login_failures")
public class LoginFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Integer failureCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_failure_time")
    private Date lastFailureTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lock_until_time")
    private Date lockUntilTime;

    public LoginFailure() {
    }

    public LoginFailure(String username) {
        this.username = username;
        this.failureCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Date getLastFailureTime() {
        return lastFailureTime;
    }

    public void setLastFailureTime(Date lastFailureTime) {
        this.lastFailureTime = lastFailureTime;
    }

    public Date getLockUntilTime() {
        return lockUntilTime;
    }

    public void setLockUntilTime(Date lockUntilTime) {
        this.lockUntilTime = lockUntilTime;
    }
}
