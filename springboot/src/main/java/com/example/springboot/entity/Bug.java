package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="bugs")
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "project_id")
    private Long projectId;
    
    @Column(name = "task_id")
    private Long taskId;
    
    @Column(name = "reporter_id")
    private Long reporterId;
    
    @Column(name = "assignee_id")
    private Long assigneeId;
    
    @Column(name = "severity")
    private Integer severity;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "bug_type")
    private String bugType;
    
    @Column(name = "created_at")
    private String createdAt;
    
    @Column(name = "resolved_at")
    private String resolvedAt;
    
    @Column(name = "solution")
    private String solution;
    
    @Column(name = "project_name")
    private String projectName;
    
    @Column(name = "deadline")
    private String deadline;

    // 构造方法
    public Bug() {
    }

    public Bug(Long id, String title, String description, Long projectId, Long taskId, Long reporterId, Long assigneeId, Integer severity, Integer status, String bugType, String createdAt, String resolvedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.taskId = taskId;
        this.reporterId = reporterId;
        this.assigneeId = assigneeId;
        this.severity = severity;
        this.status = status;
        this.bugType = bugType;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    // Getter 和 Setter 方法
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBugType() {
        return bugType;
    }

    public void setBugType(String bugType) {
        this.bugType = bugType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

}