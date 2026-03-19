package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="test_suites")
public class TestSuite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long project_id;
    private Long product_id;
    private Integer priority;
    private Integer status;
    private String start_date;
    private String end_date;
    private Long assignee_id;
    private String created_at;

    // 构造方法
    public TestSuite() {
    }

    public TestSuite(Long id, String name, Long project_id, Long product_id, Integer priority, Integer status, String start_date, String end_date, Long assignee_id, String created_at) {
        this.id = id;
        this.name = name;
        this.project_id = project_id;
        this.product_id = product_id;
        this.priority = priority;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.assignee_id = assignee_id;
        this.created_at = created_at;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Long getAssignee_id() {
        return assignee_id;
    }

    public void setAssignee_id(Long assignee_id) {
        this.assignee_id = assignee_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
