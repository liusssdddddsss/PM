package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Admin;
import com.example.springboot.entity.LoginLog;
import com.example.springboot.entity.User;
import com.example.springboot.repository.LoginLogRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.AdminService;
import com.example.springboot.util.AESUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name="管理员模块")
public class AdminController {
    @Resource
    AdminService adminService;
    
    @Resource
    LoginLogRepository loginLogRepository;
    
    @Resource
    UserRepository userRepository;

//    查询所有的数据
    @Operation(summary = "查询所有管理员",description = "返回管理员列表")
    @GetMapping("/findAll")
    public Result findAll(){
        List<Admin> list = adminService.findall();
        return  Result.success(list);
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @Operation(summary = "用户登录",description = "根据用户名和密码进行登录验证")
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.username;
        String password = loginRequest.password;
        System.out.println("接收到登录请求，用户名: " + username + "，密码: " + (password != null ? "****" : "null"));
        
        if (username == null || password == null) {
            System.out.println("用户名或密码为空");
            return Result.error("用户名和密码不能为空");
        }
        
        Admin admin = null;
        String errorMessage = "用户名或密码错误";
        
        try {
            System.out.println("调用AdminService.login方法");
            admin = adminService.login(username, password);
            System.out.println("AdminService.login返回结果: " + (admin != null ? "成功" : "失败"));
        } catch (Exception e) {
            errorMessage = e.getMessage();
            admin = null; // 确保在捕获到异常时，将admin设置为null
            System.out.println("登录失败: " + errorMessage);
        }
        
        try {
            // 记录登录日志
            LoginLog loginLog = new LoginLog();
            if (admin != null) {
                loginLog.setUser_id(admin.getUsername());
                loginLog.setStatus(1); // 1表示成功
                System.out.println("登录成功，记录日志，用户: " + admin.getUsername());
            } else {
                loginLog.setUser_id(username != null ? username : "1"); // 使用管理员账号作为未知用户的标识
                loginLog.setStatus(0); // 0表示失败
                System.out.println("登录失败，记录日志，用户: " + username + "，错误信息: " + errorMessage);
            }
            loginLog.setIp_address(getClientIp(request));
            loginLog.setUser_agent(request.getHeader("User-Agent"));
            loginLog.setLoginTime(new Date());
            loginLogRepository.save(loginLog);
            System.out.println("登录日志记录成功: " + loginLog.getId());
        } catch (Exception e) {
            System.out.println("登录日志记录失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (admin != null) {
            System.out.println("登录成功，返回用户信息");
            return Result.success(admin);
        } else {
            System.out.println("登录失败，返回错误信息: " + errorMessage);
            return Result.error(errorMessage);
        }
    }
    
    @Operation(summary = "用户退出",description = "记录用户退出时间")
    @PostMapping("/logout")
    public Result logout(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        try {
            String username = requestBody.get("username");
            System.out.println("退出请求，用户名: " + username);
            if (username != null) {
                // 查找该用户最近的一条登录日志（未记录退出时间的）
                List<LoginLog> loginLogs = loginLogRepository.findByUser_idOrderByLoginTimeDesc(username);
                System.out.println("找到登录日志数量: " + loginLogs.size());
                if (!loginLogs.isEmpty()) {
                    LoginLog loginLog = loginLogs.get(0);
                    System.out.println("最近登录日志ID: " + loginLog.getId() + ", 登录时间: " + loginLog.getLoginTime() + ", 退出时间: " + loginLog.getOutTime());
                    if (loginLog.getOutTime() == null) {
                        loginLog.setOutTime(new Date());
                        loginLogRepository.save(loginLog);
                        System.out.println("退出日志记录成功: " + loginLog.getId() + ", 退出时间: " + loginLog.getOutTime());
                    } else {
                        System.out.println("退出时间已存在，无需更新: " + loginLog.getOutTime());
                    }
                } else {
                    System.out.println("未找到该用户的登录日志: " + username);
                }
            } else {
                System.out.println("用户名为空");
            }
        } catch (Exception e) {
            System.out.println("退出日志记录失败: " + e.getMessage());
            e.printStackTrace();
        }
        return Result.success();
    }
    
    // 获取客户端IP地址
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    @Operation(summary = "批量加密用户密码", description = "对所有用户的密码进行AES加密")
    @PostMapping("/encrypt-passwords")
    public Result encryptPasswords() {
        try {
            System.out.println("开始批量加密用户密码");
            // 获取所有用户
            Iterable<User> users = userRepository.findAll();
            int encryptedCount = 0;
            
            for (User user : users) {
                try {
                    // 尝试解密密码，如果解密失败，说明密码是明文
                    AESUtil.decrypt(user.getPassword());
                    // 密码已经是加密的，跳过
                    System.out.println("用户 " + user.getUsername() + " 的密码已经是加密的，跳过");
                } catch (Exception e) {
                    // 解密失败，说明密码是明文，进行加密
                    System.out.println("用户 " + user.getUsername() + " 的密码是明文，开始加密");
                    String encryptedPassword = AESUtil.encrypt(user.getPassword());
                    user.setPassword(encryptedPassword);
                    userRepository.save(user);
                    encryptedCount++;
                    System.out.println("用户 " + user.getUsername() + " 的密码加密成功");
                }
            }
            
            System.out.println("批量加密完成，共加密 " + encryptedCount + " 个用户的密码");
            return Result.success("批量加密完成，共加密 " + encryptedCount + " 个用户的密码");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量加密失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "批量加密用户邮箱", description = "对所有用户的邮箱地址进行AES加密")
    @PostMapping("/encrypt-emails")
    public Result encryptEmails() {
        try {
            System.out.println("开始批量加密用户邮箱");
            // 获取所有用户
            Iterable<User> users = userRepository.findAll();
            int encryptedCount = 0;
            
            for (User user : users) {
                try {
                    String email = user.getEmail();
                    if (email != null && !email.isEmpty()) {
                        // 尝试解密邮箱，如果解密失败，说明邮箱是明文
                        try {
                            AESUtil.decrypt(email);
                            // 邮箱已经是加密的，跳过
                            System.out.println("用户 " + user.getUsername() + " 的邮箱已经是加密的，跳过");
                        } catch (Exception e) {
                            // 解密失败，说明邮箱是明文，进行加密
                            System.out.println("用户 " + user.getUsername() + " 的邮箱是明文，开始加密: " + email);
                            String encryptedEmail = AESUtil.encrypt(email);
                            user.setEmail(encryptedEmail);
                            userRepository.save(user);
                            encryptedCount++;
                            System.out.println("用户 " + user.getUsername() + " 的邮箱加密成功");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("加密用户 " + user.getUsername() + " 的邮箱时出错: " + e.getMessage());
                }
            }
            
            System.out.println("批量加密完成，共加密 " + encryptedCount + " 个用户的邮箱");
            return Result.success("批量加密完成，共加密 " + encryptedCount + " 个用户的邮箱");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量加密失败: " + e.getMessage());
        }
    }
}
