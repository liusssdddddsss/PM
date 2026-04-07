package com.example.springboot.service;

import com.example.springboot.entity.Admin;
import com.example.springboot.entity.User;
import com.example.springboot.repository.AdminRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.util.AESUtil;
import com.example.springboot.util.BCryptUtil;
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
                boolean passwordMatch = false;
                
                // 首先尝试BCrypt验证
                try {
                    passwordMatch = BCryptUtil.matches(password, admin.getPassword());
                    System.out.println("BCrypt密码验证结果: " + passwordMatch);
                } catch (Exception e) {
                    System.out.println("BCrypt验证异常: " + e.getMessage());
                }
                
                // 如果BCrypt验证失败，尝试AES验证
                if (!passwordMatch) {
                    try {
                        // 尝试使用AES解密验证
                        String decryptedPassword = AESUtil.decrypt(admin.getPassword());
                        System.out.println("AES解密后的密码: " + decryptedPassword);
                        passwordMatch = decryptedPassword.equals(password);
                        System.out.println("AES密码验证结果: " + passwordMatch);
                        
                        // 如果AES匹配，将密码转换为BCrypt加密后存储
                        if (passwordMatch) {
                            try {
                                admin.setPassword(BCryptUtil.encrypt(password));
                                adminRepository.save(admin);
                                System.out.println("密码已转换为BCrypt加密并更新");
                            } catch (Exception ex) {
                                System.out.println("BCrypt密码加密失败: " + ex.getMessage());
                            }
                        }
                    } catch (Exception ex2) {
                        // 解密失败，说明密码是明文，直接比较
                        System.out.println("AES解密也失败，使用明文比较: " + ex2.getMessage());
                        passwordMatch = admin.getPassword().equals(password);
                        System.out.println("明文密码是否匹配: " + passwordMatch);
                        
                        // 如果明文匹配，将密码加密后存储
                        if (passwordMatch) {
                            try {
                                admin.setPassword(BCryptUtil.encrypt(password));
                                adminRepository.save(admin);
                                System.out.println("密码已BCrypt加密并更新");
                            } catch (Exception ex3) {
                                System.out.println("密码加密失败: " + ex3.getMessage());
                            }
                        }
                    }
                }
                
                if (passwordMatch) {
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
