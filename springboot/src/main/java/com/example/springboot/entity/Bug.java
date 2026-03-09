package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="bugs")
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private Integer project_id;
    private Integer task_id;
    private Integer reporter_id;
    private Integer assignee_id;
    private Integer severity;
    private Integer status;
    private String bug_type;
    private String created_at;
    private String resolved_at;

    // 构造方法
    public Bug() {
    }

    public Bug(Integer id, String title, String description, Integer project_id, Integer task_id, Integer reporter_id, Integer assignee_id, Integer severity, Integer status, String bug_type, String created_at, String resolved_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.project_id = project_id;
        this.task_id = task_id;
        this.reporter_id = reporter_id;
        this.assignee_id = assignee_id;
        this.severity = severity;
        this.status = status;
        this.bug_type = bug_type;
        this.created_at = created_at;
        this.resolved_at = resolved_at;
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

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public Integer getReporter_id() {
        return reporter_id;
    }

    public void setReporter_id(Integer reporter_id) {
        this.reporter_id = reporter_id;
    }

    public Integer getAssignee_id() {
        return assignee_id;
    }

    public void setAssignee_id(Integer assignee_id) {
        this.assignee_id = assignee_id;
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

    public String getBug_type() {
        return bug_type;
    }

    public void setBug_type(String bug_type) {
        this.bug_type = bug_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getResolved_at() {
        return resolved_at;
    }

    public void setResolved_at(String resolved_at) {
        this.resolved_at = resolved_at;
    }

}