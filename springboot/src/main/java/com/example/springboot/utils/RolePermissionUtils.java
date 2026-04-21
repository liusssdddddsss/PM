package com.example.springboot.utils;

import com.example.springboot.entity.Project;
import com.example.springboot.entity.ProjectMember;
import com.example.springboot.entity.User;
import com.example.springboot.service.ProjectMemberService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RolePermissionUtils {
    
    // 角色定义
    public static final Long ROLE_PRODUCT_MANAGER = 1L; // 产品经理
    public static final Long ROLE_DEVELOPER = 2L; // 开发者
    public static final Long ROLE_TESTER = 3L; // 测试者
    public static final Long ROLE_ADMIN = 99L; // 管理员
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ProjectMemberService projectMemberService;
    
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
        User user = userService.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        // 获取项目信息
        java.util.Optional<Project> optionalProject = projectService.findById(projectId);
        if (!optionalProject.isPresent()) {
            return false;
        }
        Project project = optionalProject.get();
        
        // 检查用户是否是项目的创建者或管理者
        Long userId = null;
        try {
            userId = Long.parseLong(user.getUsername());
        } catch (NumberFormatException e) {
            return false;
        }
        
        if (userId != null && (userId.equals(project.getCreator_id()) || userId.equals(project.getManagerId()))) {
            return true;
        }
        
        // 检查用户是否是项目的成员
        try {
            List<ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
            for (ProjectMember member : projectMembers) {
                if (member.getProjectId() != null && member.getProjectId().equals(projectId)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 检查失败，返回false
        }
        
        return false;
    }
}
