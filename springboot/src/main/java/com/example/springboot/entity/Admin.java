package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="users")
//属性要与数据库对应上
public class Admin {

    @Id
    private Integer id;
    private Long username;
    private String name;
    private String password;
    private Integer is_admin;
    private String avatar;

    // 构造方法
    public Admin() {
    }

    public Admin(Integer id, Long username, String name, String password, Integer is_admin, String avatar) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.is_admin = is_admin;
        this.avatar = avatar;
    }

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUsername() {
        return username;
    }

    public void setUsername(Long username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Integer is_admin) {
        this.is_admin = is_admin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
