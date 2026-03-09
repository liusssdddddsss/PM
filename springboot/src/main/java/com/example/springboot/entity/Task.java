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
    private Integer project_id;
    private Integer requirement_id;
    private Integer parent_id;
    private Integer creator_id;
    private Integer assignee_id;
    private Integer priority;
    private Integer status;
    private Integer progress;
    private Double estimated_hours;
    private Double actual_hours;
    private String start_date;
    private String due_date;
    private String created_at;

    // 构造方法
    public Task() {
    }

    public Task(Integer id, String title, String description, Integer project_id, Integer requirement_id, Integer parent_id, Integer creator_id, Integer assignee_id, Integer priority, Integer status, Integer progress, Double estimated_hours, Double actual_hours, String start_date, String due_date, String created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.project_id = project_id;
        this.requirement_id = requirement_id;
        this.parent_id = parent_id;
        this.creator_id = creator_id;
        this.assignee_id = assignee_id;
        this.priority = priority;
        this.status = status;
        this.progress = progress;
        this.estimated_hours = estimated_hours;
        this.actual_hours = actual_hours;
        this.start_date = start_date;
        this.due_date = due_date;
        this.created_at = created_at;
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

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public Integer getRequirement_id() {
        return requirement_id;
    }

    public void setRequirement_id(Integer requirement_id) {
        this.requirement_id = requirement_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Integer creator_id) {
        this.creator_id = creator_id;
    }

    public Integer getAssignee_id() {
        return assignee_id;
    }

    public void setAssignee_id(Integer assignee_id) {
        this.assignee_id = assignee_id;
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

    public Double getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(Double estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public Double getActual_hours() {
        return actual_hours;
    }

    public void setActual_hours(Double actual_hours) {
        this.actual_hours = actual_hours;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}