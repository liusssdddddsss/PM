package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.User;
import com.example.springboot.service.TeamMemberService;
import com.example.springboot.service.TeamService;
import com.example.springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户相关模块")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private TeamMemberService teamMemberService;

    @Operation(summary = "获取用户所在团队的产品经理", description = "根据用户ID获取其所在团队的所有产品经理")
    @GetMapping("/{userId}/product-managers")
    public Result getProductManagers(@PathVariable String userId) {
        try {
            System.out.println("获取用户产品经理，用户ID: " + userId);
            
            List<Map<String, Object>> productManagers = new ArrayList<>();
            
            // 查找用户
            User user = userService.findByUsername(userId);
            if (user == null) {
                System.out.println("用户不存在");
                return Result.success(productManagers);
            }
            
            Long userLongId = null;
            try {
                userLongId = Long.parseLong(userId);
            } catch (NumberFormatException e) {
                // 如果userId不是数字，尝试用username查找用户，然后获取用户ID
                try {
                    userLongId = Long.parseLong(user.getUsername());
                } catch (NumberFormatException ex) {
                    System.out.println("用户ID格式错误");
                    return Result.success(productManagers);
                }
            }
            
            System.out.println("用户ID: " + userLongId);
            
            // 获取用户所在的所有团队
            List<TeamMember> userTeams = teamMemberService.findByUserId(userLongId);
            System.out.println("用户加入的团队数量: " + userTeams.size());
            
            // 收集所有团队中的产品经理
            Map<Long, User> productManagerMap = new HashMap<>();
            
            for (TeamMember userTeam : userTeams) {
                Integer teamId = userTeam.getTeamId();
                System.out.println("检查团队ID: " + teamId);
                
                // 获取团队的所有成员
                List<TeamMember> teamMembers = teamMemberService.findByTeamId(teamId);
                System.out.println("团队 " + teamId + " 的成员数量: " + teamMembers.size());
                
                for (TeamMember member : teamMembers) {
                    Long memberUserId = member.getUserId();
                    System.out.println("检查团队成员: " + memberUserId);
                    
                    if (memberUserId == null) continue;
                    
                    // 查找该成员用户
                    User memberUser = userService.findById(memberUserId.toString()).orElse(null);
                    if (memberUser == null) {
                        System.out.println("成员用户不存在");
                        continue;
                    }
                    
                    // 检查是否是产品经理（role_id=2）或管理员（is_admin=1）
                    boolean isProductManager = (memberUser.getRole_id() != null && memberUser.getRole_id() == 2L) 
                            || (memberUser.getIs_admin() != null && memberUser.getIs_admin() == 1);
                    
                    System.out.println("成员 " + memberUser.getName() + " 是产品经理: " + isProductManager 
                            + ", role_id: " + memberUser.getRole_id() 
                            + ", is_admin: " + memberUser.getIs_admin());
                    
                    if (isProductManager && !productManagerMap.containsKey(memberUserId)) {
                        productManagerMap.put(memberUserId, memberUser);
                    }
                }
            }
            
            // 转换为返回格式
            for (User pm : productManagerMap.values()) {
                Map<String, Object> pmMap = new HashMap<>();
                pmMap.put("id", pm.getUsername()); // 使用username作为ID
                pmMap.put("name", pm.getName());
                pmMap.put("username", pm.getUsername());
                productManagers.add(pmMap);
                System.out.println("添加产品经理: " + pm.getName());
            }
            
            System.out.println("最终返回产品经理数量: " + productManagers.size());
            return Result.success(productManagers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品经理失败: " + e.getMessage());
        }
    }
}
