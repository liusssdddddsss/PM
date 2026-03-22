package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.OptionLog;
import com.example.springboot.repository.OptionLogRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/logs")
public class AdminLogController {

    @Resource
    private OptionLogRepository optionLogRepository;

    @GetMapping
    public Result listLogs(@RequestParam(required = false) Integer limit) {
        try {
            List<OptionLog> logs = optionLogRepository.findAll();
            logs.sort((a, b) -> {
                Date da = a.getCreatedAt();
                Date db = b.getCreatedAt();
                if (da == null && db == null) return 0;
                if (da == null) return 1;
                if (db == null) return -1;
                return db.compareTo(da);
            });

            if (limit != null && limit > 0 && logs.size() > limit) {
                logs = logs.subList(0, limit);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (OptionLog l : logs) {
                result.add(toLogRow(l));
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/summary")
    public Result logSummary() {
        try {
            List<OptionLog> logs = optionLogRepository.findAll();
            int totalLogs = logs.size();

            LocalDate today = LocalDate.now();
            ZoneId zone = ZoneId.systemDefault();

            List<OptionLog> todayLogsList = logs.stream().filter(l -> {
                Date d = l.getCreatedAt();
                if (d == null) return false;
                LocalDate ld = d.toInstant().atZone(zone).toLocalDate();
                return ld.equals(today);
            }).collect(Collectors.toList());

            int todayLogs = todayLogsList.size();

            Set<String> activeUsers = todayLogsList.stream()
                    .map(OptionLog::getOperator)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            long errorLogs = todayLogsList.stream()
                    .filter(l -> {
                        String action = l.getAction();
                        return action != null && (action.contains("失败") || action.contains("错误"));
                    })
                    .count();

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalLogs", totalLogs);
            summary.put("todayLogs", todayLogs);
            summary.put("activeUsers", activeUsers.size());
            summary.put("errorLogs", (int) errorLogs);

            return Result.success(summary);
        } catch (Exception e) {
            return Result.error("查询日志统计失败: " + e.getMessage());
        }
    }

    private Map<String, Object> toLogRow(OptionLog l) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", l.getId());
        row.put("teamId", "");
        row.put("teamName", "");
        // 前端筛选“用户”时用的是 userId/userName，这里用 operator 作为兜底
        row.put("userId", l.getOperator());
        row.put("userName", l.getOperator());
        row.put("action", l.getAction());
        row.put("actionType", classifyActionType(l.getAction()));

        String created = formatDateTime(l.getCreatedAt());
        row.put("loginTime", created);
        row.put("logoutTime", "");
        row.put("actionTime", created);

        row.put("submitCount", 0);
        row.put("ipAddress", "");

        row.put("status", "成功");
        return row;
    }

    private static String formatDateTime(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }

    private static String classifyActionType(String action) {
        if (action == null) return "other";
        if (action.contains("登录")) return "login";
        if (action.contains("退出") || action.contains("登出")) return "logout";
        if (action.contains("提交") || action.contains("新增") || action.contains("创建")) return "submit";
        if (action.contains("修改") || action.contains("更新") || action.contains("编辑")) return "update";
        if (action.contains("删除") || action.contains("移除")) return "delete";
        if (action.contains("失败") || action.contains("错误")) return "other";
        return "other";
    }
}

