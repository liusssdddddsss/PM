package com.example.springboot.service;

import com.example.springboot.entity.Admin;
import com.example.springboot.repository.AdminRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Resource
    AdminRepository adminRepository;

    public List<Admin> findall(){
        return adminRepository.findAll();
    }

    public Admin login(String username, String password) {
        try {
            Admin admin = adminRepository.findByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                return admin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
