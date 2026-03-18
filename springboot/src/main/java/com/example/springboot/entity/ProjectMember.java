package com.example.springboot.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "project_members")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "role_type")
    private String roleType;
    @Column(name = "joined_at")
    private Date joinedAt;
    @Column(name = "joined_by")
    private Long joinedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Long getJoinedBy() {
        return joinedBy;
    }

    public void setJoinedBy(Long joinedBy) {
        this.joinedBy = joinedBy;
    }
}
