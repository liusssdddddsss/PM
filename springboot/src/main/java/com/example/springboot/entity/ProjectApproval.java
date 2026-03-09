package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="project_approvals")
//属性要与数据库对应上
public class ProjectApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer project_id;
    private Integer approver_id;
    private String action;
    private String type;
    private String comment;
    private String created_at;

    // 构造方法
    public ProjectApproval() {
    }

    public ProjectApproval(Integer id, Integer project_id, Integer approver_id, String action, String type, String comment, String created_at) {
        this.id = id;
        this.project_id = project_id;
        this.approver_id = approver_id;
        this.action = action;
        this.type = type;
        this.comment = comment;
        this.created_at = created_at;
    }

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public Integer getApprover_id() {
        return approver_id;
    }

    public void setApprover_id(Integer approver_id) {
        this.approver_id = approver_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}