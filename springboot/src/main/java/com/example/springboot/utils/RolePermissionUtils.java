package com.example.springboot.utils;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RolePermissionUtils {
    
    // 角色定义
    public static final Long ROLE_PRODUCT_MANAGER = 1L; // 产品经理
    public static final Long ROLE_DEVELOPER = 2L; // 开发者
    public static final Long ROLE_TESTER = 3L; // 测试者
    public static final Long ROLE_ADMIN = 99L; // 管理员
    
    @Autowired
    private UserService userService;
    
    /**
     * 检查用户是否为产品经理
     */
    public boolean isProductManager(String username) {
        User user = userService.findByUsername(username);
        return user != null && ROLE_PRODUCT_MANAGER.equals(user.getRole_id());
    }
    
    /**
     * 检查用户是否为开发者
     */
    public boolean isDeveloper(String username) {
        User user = userService.findByUsername(username);
        return user != null && ROLE_DEVELOPER.equals(user.getRole_id());
    }
    
    /**
     * 检查用户是否为测试者
     */
    public boolean isTester(String username) {
        User user = userService.findByUsername(username);
        return user != null && ROLE_TESTER.equals(user.getRole_id());
    }
    
    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin(String username) {
        User user = userService.findByUsername(username);
        return user != null && (ROLE_ADMIN.equals(user.getRole_id()) || user.getIs_admin() != null && user.getIs_admin() == 1);
    }
    
    /**
     * 获取用户角色名称
     */
    public String getRoleName(String username) {
        User user = userService.findByUsername(username);
        if (user == null || user.getRole_id() == null) {
            return "普通用户";
        }
        
        switch (user.getRole_id().intValue()) {
            case 1:
                return "产品经理";
            case 2:
                return "开发者";
            case 3:
                return "测试者";
            case 99:
                return "管理员";
            default:
                return "普通用户";
        }
    }
    
    /**
     * 检查用户是否有权限访问项目
     */
    public boolean hasProjectAccess(String username, Long projectId) {
        // 管理员和产品经理可以访问所有项目
        if (isAdmin(username) || isProductManager(username)) {
            return true;
        }
        
        // 开发者和测试者只能访问自己参与的项目
        // 这里可以根据实际的项目成员关系进行检查
        // 暂时返回true，后续可以根据实际需求实现
        return true;
    }
}
