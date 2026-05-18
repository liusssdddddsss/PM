package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "solution", columnDefinition = "TEXT")
    private String solution;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "related_objects")
    private Integer relatedObjects;

    @Column(name = "reporter", length = 255)
    private String reporter;

    @Column(name = "cc", length = 500)
    private String cc;

    @Column(name = "product_id")
    private Long productId;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}