package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Feedback;
import com.example.springboot.entity.OptionLog;
import com.example.springboot.repository.FeedbackRepository;
import com.example.springboot.repository.OptionLogRepository;
import com.example.springboot.repository.TeamRepository;
import com.example.springboot.repository.UserRepository;
import jakarta.annotation.Resource;
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
    @Resource
    private OptionLogRepository optionLogRepository;
    @Resource
    private FeedbackRepository feedbackRepository;

    @GetMapping("/summary")
    public Result summary() {
        try {
            long userCount = userRepository.count();
            long teamCount = teamRepository.count();
            long logCount = optionLogRepository.count();
            long feedbackCount = feedbackRepository.count();

            List<OptionLog> latestLogs = optionLogRepository.findTop20ByOrderByCreatedAtDesc();
            List<Map<String, Object>> recentActivities = new ArrayList<>();
            for (OptionLog l : latestLogs) {
                Map<String, Object> row = new HashMap<>();
                row.put("time", formatDateTime(l.getCreatedAt()));
                row.put("action", l.getAction());
                row.put("user", l.getOperator());
                row.put("status", "成功");
                recentActivities.add(row);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("userCount", userCount);
            data.put("teamCount", teamCount);
            data.put("logCount", logCount);
            data.put("feedbackCount", feedbackCount);
            data.put("recentActivities", recentActivities);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取管理员面板统计失败: " + e.getMessage());
        }
    }

    private static String formatDateTime(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d);
    }
}

