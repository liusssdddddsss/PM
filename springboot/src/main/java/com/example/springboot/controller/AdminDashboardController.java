package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Feedback;
import com.example.springboot.entity.OperationLog;
import com.example.springboot.entity.User;
import com.example.springboot.repository.FeedbackRepository;
import com.example.springboot.repository.TeamRepository;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.service.OperationLogService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private TeamRepository teamRepository;
    @Autowired
    private OperationLogService operationLogService;
    @Resource
    private FeedbackRepository feedbackRepository;

    @GetMapping("/summary")
    public Result summary() {
        try {
            // 统计非管理员用户数量
            long userCount = userRepository.countNonAdminUsers();
            long teamCount = teamRepository.count();
            long logCount = 0;
            List<Map<String, Object>> recentActivities = new ArrayList<>();
            
            // 尝试获取操作日志，处理表不存在的情况
            try {
                List<OperationLog> operationLogs = operationLogService.findall();
                logCount = operationLogs.size();
                
                // 按时间倒序排序，取最新的20条
                operationLogs.sort((o1, o2) -> {
                    if (o1.getCreated_at() == null || o2.getCreated_at() == null) {
                        return 0;
                    }
                    return o2.getCreated_at().compareTo(o1.getCreated_at());
                });
                
                // 只取前20条
                List<OperationLog> latestLogs = operationLogs.stream().limit(20).toList();
                
                for (OperationLog log : latestLogs) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("time", formatDateTime(log.getCreated_at()));
                    row.put("action", log.getAction() != null ? log.getAction() : "");
                    
                    // 获取操作人名称
                    String operator = "未知用户";
                    if (log.getUser_id() != null) {
                        try {
                            // 直接将user_id转换为字符串作为用户名显示，因为user_id已经是工号
                            operator = String.valueOf(log.getUser_id());
                        } catch (Exception e) {
                            // 获取用户信息失败，使用默认值
                        }
                    }
                    row.put("user", operator);
                    row.put("status", "成功");
                    recentActivities.add(row);
                }
            } catch (Exception e) {
                // 操作日志表不存在，使用空数据
                logCount = 0;
                recentActivities = new ArrayList<>();
            }
            
            long feedbackCount = 0;
            // 尝试获取反馈数量，处理表不存在的情况
            try {
                feedbackCount = feedbackRepository.count();
            } catch (Exception e) {
                feedbackCount = 0;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("userCount", userCount);
            data.put("teamCount", teamCount);
            data.put("logCount", logCount);
            data.put("feedbackCount", feedbackCount);
            data.put("recentActivities", recentActivities);

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取管理员面板统计失败: " + e.getMessage());
        }
    }

    private static String formatDateTime(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d);
    }
}

