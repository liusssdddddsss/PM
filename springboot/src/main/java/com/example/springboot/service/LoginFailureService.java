package com.example.springboot.service;

import com.example.springboot.entity.LoginFailure;
import com.example.springboot.entity.User;
import com.example.springboot.repository.LoginFailureRepository;
import com.example.springboot.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class LoginFailureService {
    
    @Resource
    private LoginFailureRepository loginFailureRepository;
    
    @Resource
    private UserRepository userRepository;
    
    // 最大失败次数
    private static final int MAX_FAILURE_COUNT = 5;
    
    // 锁定时间（分钟）
    private static final int LOCK_MINUTES = 30;
    
    /**
     * 检查账号是否被锁定
     */
    public boolean isAccountLocked(String username) {
        Optional<LoginFailure> optional = loginFailureRepository.findByUsername(username);
        if (optional.isPresent()) {
            LoginFailure loginFailure = optional.get();
            Date lockUntil = loginFailure.getLockUntilTime();
            if (lockUntil != null && lockUntil.after(new Date())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取账号解锁时间
     */
    public Date getLockUntilTime(String username) {
        Optional<LoginFailure> optional = loginFailureRepository.findByUsername(username);
        if (optional.isPresent()) {
            return optional.get().getLockUntilTime();
        }
        return null;
    }
    
    /**
     * 获取剩余锁定时间（分钟）
     */
    public long getRemainingLockMinutes(String username) {
        Optional<LoginFailure> optional = loginFailureRepository.findByUsername(username);
        if (optional.isPresent()) {
            Date lockUntil = optional.get().getLockUntilTime();
            if (lockUntil != null && lockUntil.after(new Date())) {
                long diff = lockUntil.getTime() - new Date().getTime();
                return Math.max(1, diff / (1000 * 60));
            }
        }
        return 0;
    }
    
    /**
     * 记录登录失败
     * @return 是否需要锁定账号
     */
    public boolean recordFailure(String username) {
        LoginFailure loginFailure = loginFailureRepository.findByUsername(username)
                .orElse(new LoginFailure(username));
        
        // 如果已经被锁定且还在锁定期内，直接返回
        Date lockUntil = loginFailure.getLockUntilTime();
        if (lockUntil != null && lockUntil.after(new Date())) {
            return true;
        }
        
        // 如果锁定时间已过，重置失败次数
        if (lockUntil != null && !lockUntil.after(new Date())) {
            loginFailure.setFailureCount(0);
            loginFailure.setLockUntilTime(null);
        }
        
        // 增加失败次数
        int newCount = (loginFailure.getFailureCount() == null ? 0 : loginFailure.getFailureCount()) + 1;
        loginFailure.setFailureCount(newCount);
        loginFailure.setLastFailureTime(new Date());
        
        System.out.println("用户 " + username + " 登录失败次数: " + newCount);
        
        // 如果达到最大失败次数，锁定账号
        if (newCount >= MAX_FAILURE_COUNT) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, LOCK_MINUTES);
            loginFailure.setLockUntilTime(calendar.getTime());
            System.out.println("用户 " + username + " 已被锁定，解锁时间: " + calendar.getTime());
            
            // 同时更新User表的status字段（可选，用于额外的锁定标记）
            try {
                User user = userRepository.findByUsername(username);
                if (user != null && user.getStatus() != null && user.getStatus() == 1) {
                    // 暂时只使用login_failures表，不修改用户原本的状态
                    System.out.println("用户状态保持不变");
                }
            } catch (Exception e) {
                System.out.println("更新用户状态失败: " + e.getMessage());
            }
        }
        
        loginFailureRepository.save(loginFailure);
        return newCount >= MAX_FAILURE_COUNT;
    }
    
    /**
     * 登录成功后清除失败记录
     */
    public void clearFailures(String username) {
        Optional<LoginFailure> optional = loginFailureRepository.findByUsername(username);
        if (optional.isPresent()) {
            LoginFailure loginFailure = optional.get();
            loginFailure.setFailureCount(0);
            loginFailure.setLockUntilTime(null);
            loginFailure.setLastFailureTime(null);
            loginFailureRepository.save(loginFailure);
            System.out.println("用户 " + username + " 登录成功，清除失败记录");
        }
    }
    
    /**
     * 获取当前失败次数
     */
    public int getFailureCount(String username) {
        Optional<LoginFailure> optional = loginFailureRepository.findByUsername(username);
        if (optional.isPresent()) {
            LoginFailure loginFailure = optional.get();
            // 如果锁定时间已过，返回0
            Date lockUntil = loginFailure.getLockUntilTime();
            if (lockUntil != null && !lockUntil.after(new Date())) {
                return 0;
            }
            return loginFailure.getFailureCount() == null ? 0 : loginFailure.getFailureCount();
        }
        return 0;
    }
}
