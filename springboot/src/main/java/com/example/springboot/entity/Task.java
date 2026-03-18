package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="tasks")
//属性要与数据库对应上
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private Integer projectId;
    private Integer requirementId;
    private Integer parentId;
    private Integer iterationId;
    private Integer creatorId;
    private Integer assigneeId;
    private Integer priority;
    private Integer status;
    private Integer progress;
    private Double estimatedHours;
    private Double actualHours;
    private String startDate;
    private String dueDate;
    private String createdAt;

    // 构造方法
    public Task() {
    }

    public Task(Integer id, String title, String description, Integer projectId, Integer requirementId, Integer parentId, Integer iterationId, Integer creatorId, Integer assigneeId, Integer priority, Integer status, Integer progress, Double estimatedHours, Double actualHours, String startDate, String dueDate, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.requirementId = requirementId;
        this.parentId = parentId;
        this.iterationId = iterationId;
        this.creatorId = creatorId;
        this.assigneeId = assigneeId;
        this.priority = priority;
        this.status = status;
        this.progress = progress;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Integer requirementId) {
        this.requirementId = requirementId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getIterationId() {
        return iterationId;
    }

    public void setIterationId(Integer iterationId) {
        this.iterationId = iterationId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}