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
    
    // 角色定义 - 与数据库中实际存储的数据一致
    public static final Long ROLE_PRODUCT_MANAGER = 1L; // 产品经理
    public static final Long ROLE_DEVELOPER = 2L; // 开发者
    public static final Long ROLE_TESTER = 3L; // 测试者
    // 注意：管理员不使用role_id，而是使用is_admin字段
    
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
        if (user != null) {
            System.out.println("用户 " + username + " 的信息:");
            System.out.println("  用户ID: " + user.getId());
            System.out.println("  用户名(username): " + user.getUsername());
            System.out.println("  姓名: " + user.getName());
            System.out.println("  role_id: " + user.getRole_id());
            System.out.println("  is_admin: " + user.getIs_admin());
            System.out.println("  是否为产品经理: " + ROLE_PRODUCT_MANAGER.equals(user.getRole_id()));
        } else {
            System.out.println("用户 " + username + " 不存在");
        }
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
        if (user != null) {
            System.out.println("检查用户 " + username + " 是否为管理员:");
            System.out.println("  user.getIs_admin() != null: " + (user.getIs_admin() != null));
            System.out.println("  user.getIs_admin() == 1: " + (user.getIs_admin() != null && user.getIs_admin() == 1));
        }
        return user != null && user.getIs_admin() != null && user.getIs_admin() == 1;
    }
    
    /**
     * 获取用户角色名称
     */
    public String getRoleName(String username) {
        User user = userService.findByUsername(username);
        if (user != null && user.getIs_admin() != null && user.getIs_admin() == 1) {
            return "管理员";
        }
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
            default:
                return "普通用户";
        }
    }
    
    /**
     * 检查用户是否有权限访问项目
     */
    public boolean hasProjectAccess(String username, Long projectId) {
        System.out.println("========== 权限检查开始 ==========");
        System.out.println("用户名: " + username + ", 项目ID: " + projectId);
        
        // 管理员和产品经理可以访问所有项目
        boolean isAdminUser = isAdmin(username);
        boolean isProductManagerUser = isProductManager(username);
        System.out.println("是否管理员: " + isAdminUser + ", 是否产品经理: " + isProductManagerUser);
        
        if (isAdminUser || isProductManagerUser) {
            System.out.println("是管理员或产品经理，允许访问所有项目");
            return true;
        }
        
        // 开发者和测试者只能访问自己参与的项目
        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("用户不存在");
            return false;
        }
        
        // 获取项目信息
        java.util.Optional<Project> optionalProject = projectService.findById(projectId);
        if (!optionalProject.isPresent()) {
            System.out.println("项目不存在");
            return false;
        }
        Project project = optionalProject.get();
        
        // 检查用户是否是项目的创建者或管理者
        Long userId = null;
        try {
            userId = Long.parseLong(user.getUsername());
        } catch (NumberFormatException e) {
            System.out.println("用户名格式错误: " + user.getUsername());
            return false;
        }
        
        System.out.println("用户ID: " + userId + ", 项目创建者: " + project.getCreator_id() + ", 项目管理者: " + project.getManagerId());
        
        if (userId != null && (userId.equals(project.getCreator_id()) || userId.equals(project.getManagerId()))) {
            System.out.println("用户是项目创建者或管理者，允许访问");
            return true;
        }
        
        // 检查用户是否是项目的成员
        try {
            List<ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
            System.out.println("用户参与的项目数量: " + projectMembers.size());
            for (ProjectMember member : projectMembers) {
                System.out.println("项目成员记录: 项目ID=" + member.getProjectId());
                if (member.getProjectId() != null && member.getProjectId().equals(projectId)) {
                    System.out.println("用户是项目成员，允许访问");
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("检查项目成员失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("用户无权限访问该项目");
        return false;
    }
}
