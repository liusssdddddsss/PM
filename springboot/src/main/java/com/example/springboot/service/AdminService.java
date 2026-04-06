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

    public Admin login(String username, String password) throws Exception {
        try {
            System.out.println("登录请求，用户名: " + username);
            Admin admin = adminRepository.findByUsername(username);
            System.out.println("Admin查询结果: " + (admin != null ? "存在" : "不存在"));
            
            if (admin != null) {
                System.out.println("Admin密码: " + admin.getPassword());
                System.out.println("输入密码: " + password);
                System.out.println("密码是否匹配: " + admin.getPassword().equals(password));
            }
            
            if (admin != null && admin.getPassword().equals(password)) {
                // 检查用户状态，确保启用的用户才能登录
                System.out.println("密码匹配，开始检查用户状态");
                User user = userRepository.findByUsername(username);
                System.out.println("User查询结果: " + (user != null ? "存在" : "不存在"));
                
                if (user != null) {
                    System.out.println("用户状态: " + user.getStatus());
                    if (user.getStatus() != null) {
                        System.out.println("用户状态值: " + user.getStatus());
                        if (user.getStatus() == 1) {
                            System.out.println("用户状态正常，允许登录");
                            return admin;
                        } else {
                            // 用户被禁用
                            System.out.println("用户被禁用，禁止登录");
                            throw new Exception("账号被封禁");
                        }
                    } else {
                        System.out.println("用户状态为null");
                        throw new Exception("用户状态未设置");
                    }
                } else {
                    // 用户在Admin表中存在，但在User表中不存在
                    System.out.println("用户在Admin表中存在，但在User表中不存在");
                    throw new Exception("用户信息不完整");
                }
            } else {
                // 用户名或密码错误
                System.out.println("用户名或密码错误");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
