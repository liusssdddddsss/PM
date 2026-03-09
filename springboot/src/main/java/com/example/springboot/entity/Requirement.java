package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="requirements")
//属性要与数据库对应上
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    private Integer type; // 1：研发需求，2：用户需求，3：业务需求
    private Integer priority;
    private Integer status;
    private Integer project_id;
    private Integer creator_id;
    private Integer assignee_id;
    private Integer approver_id;
    private Double estimated_hours;
    private String created_at;

    // 构造方法
    public Requirement() {
    }

    public Requirement(Integer id, String title, String content, Integer type, Integer priority, Integer status, Integer project_id, Integer creator_id, Integer assignee_id, Integer approver_id, Double estimated_hours, String created_at) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.project_id = project_id;
        this.creator_id = creator_id;
        this.assignee_id = assignee_id;
        this.approver_id = approver_id;
        this.estimated_hours = estimated_hours;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
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

    public Integer getApprover_id() {
        return approver_id;
    }

    public void setApprover_id(Integer approver_id) {
        this.approver_id = approver_id;
    }

    public Double getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(Double estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}