package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Resource
    private UserRepository userRepository;

    @GetMapping
    public Result listUsers() {
        try {
            var users = userRepository.findAll();
            var result = new java.util.ArrayList<java.util.Map<String, Object>>();
            for (User u : users) {
                var row = new java.util.HashMap<String, Object>();
                row.put("userId", u.getUsername());
                row.put("name", u.getName());
                row.put("status", mapStatus(u.getStatus()));
                row.put("position", mapPosition(u.getIs_admin(), u.getRole_id()));
                result.add(row);
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询用户失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result createUser(@RequestBody AdminUserSaveRequest request) {
        try {
            if (request == null || request.userId == null || request.name == null) {
                return Result.error("参数不完整");
            }

            if (userRepository.findById(request.userId).isPresent()) {
                return Result.error("用户已存在");
            }

            User u = new User();
            u.setUsername(request.userId);
            u.setName(request.name);
            u.setPassword(request.password);
            u.setStatus(mapStatusValue(request.status));
            mapPositionToUser(u, request.position);

            u.setCreated_at(new Date());
            u.setLast_login(null);

            User saved = userRepository.save(u);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable String userId, @RequestBody AdminUserSaveRequest request) {
        try {
            Optional<User> opt = userRepository.findById(userId);
            if (opt.isEmpty()) {
                return Result.error("用户不存在");
            }
            User u = opt.get();

            if (request.name != null) {
                u.setName(request.name);
            }
            if (request.password != null && !request.password.isEmpty()) {
                u.setPassword(request.password);
            }

            if (request.status != null) {
                u.setStatus(mapStatusValue(request.status));
            }
            if (request.position != null) {
                mapPositionToUser(u, request.position);
            }

            User saved = userRepository.save(u);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/status")
    public Result updateUserStatus(@PathVariable String userId, @RequestBody AdminUserStatusRequest request) {
        try {
            Optional<User> opt = userRepository.findById(userId);
            if (opt.isEmpty()) {
                return Result.error("用户不存在");
            }
            User u = opt.get();
            u.setStatus(mapStatusValue(request.status));
            User saved = userRepository.save(u);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("更新状态失败: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/password")
    public Result changePassword(@PathVariable String userId, @RequestBody AdminPasswordRequest request) {
        try {
            Optional<User> opt = userRepository.findById(userId);
            if (opt.isEmpty()) {
                return Result.error("用户不存在");
            }
            User u = opt.get();
            if (request == null || request.password == null || request.password.isEmpty()) {
                return Result.error("密码不能为空");
            }
            u.setPassword(request.password);
            User saved = userRepository.save(u);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable String userId) {
        try {
            if (userRepository.findById(userId).isEmpty()) {
                return Result.error("用户不存在");
            }
            userRepository.deleteById(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    private static Integer mapStatusValue(String status) {
        // 前端: 启用/禁用
        if (status == null) return 1;
        if ("禁用".equals(status)) return 0;
        return 1;
    }

    private static String mapStatus(Integer statusValue) {
        // 后端: 1启用，其它视为禁用
        return statusValue != null && statusValue == 1 ? "启用" : "禁用";
    }

    private static String mapPosition(Integer isAdmin, Long roleId) {
        if (isAdmin != null && isAdmin == 1) return "管理员";
        if (roleId == null) return "用户";
        return switch (roleId.intValue()) {
            case 1 -> "产品经理";
            case 2 -> "开发者";
            case 3 -> "测试者";
            default -> "用户";
        };
    }

    private static void mapPositionToUser(User u, String position) {
        // 前端: 管理员/产品经理/开发者/测试者
        if (position == null) return;
        switch (position) {
            case "管理员" -> {
                u.setIs_admin(1);
                u.setRole_id(null);
            }
            case "产品经理" -> {
                u.setIs_admin(0);
                u.setRole_id(1L);
            }
            case "开发者" -> {
                u.setIs_admin(0);
                u.setRole_id(2L);
            }
            case "测试者" -> {
                u.setIs_admin(0);
                u.setRole_id(3L);
            }
            default -> {
                u.setIs_admin(0);
                u.setRole_id(null);
            }
        }
    }

    public static class AdminUserSaveRequest {
        public String userId;
        public String name;
        public String password;
        public String position;
        public String status;
    }

    public static class AdminUserStatusRequest {
        public String status;
    }

    public static class AdminPasswordRequest {
        public String password;
    }
}

