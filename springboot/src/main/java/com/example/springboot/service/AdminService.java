package com.example.springboot.service;

import com.example.springboot.entity.Admin;
import com.example.springboot.entity.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Resource
    AdminRepository adminRepository;
    
    @Resource
    UserRepository userRepository;

    public List<Admin> findall(){
        return adminRepository.findAll();
    }

    public Admin login(String username, String password) {
        try {
            Admin admin = adminRepository.findByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                // 检查用户状态，确保启用的用户才能登录
                User user = userRepository.findByUsername(username);
                if (user != null && user.getStatus() != null && user.getStatus() == 1) {
                    return admin;
                } else {
                    // 用户被禁用
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
