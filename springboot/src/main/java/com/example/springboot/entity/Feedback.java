package com.example.springboot.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Integer priority; // 优先级 1-5
    private String status; // 待处理、处理中、待关闭、待评审
    private String type; // 反馈类型
    @Column(name = "assignee_id")
    private Long assigneeId; // 指派给
    private String solution; // 处理方案
    @Column(name = "creator_id")
    private Long creatorId; // 创建者ID
    @Column(name = "created_at")
    private Date createdAt; // 创建时间
    @Column(name = "updated_at")
    private Date updatedAt; // 最后操作时间
    @Column(name = "related_objects")
    private Integer relatedObjects; // 关联对象数量

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getRelatedObjects() {
        return relatedObjects;
    }

    public void setRelatedObjects(Integer relatedObjects) {
        this.relatedObjects = relatedObjects;
    }
}
