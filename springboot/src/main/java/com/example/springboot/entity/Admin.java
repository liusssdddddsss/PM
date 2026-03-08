package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="users")
//属性要与数据库对应上
public class Admin {

    private Integer id;
    
    @Id
    private String username;
    
    private String password;
    private Integer is_admin;

    // 构造方法
    public Admin() {
    }

    public Admin(Integer id, String username, String password, Integer is_admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.is_admin = is_admin;
    }

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
