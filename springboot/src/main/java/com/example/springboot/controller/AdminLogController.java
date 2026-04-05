package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.LoginLog;
import com.example.springboot.entity.OperationLog;
import com.example.springboot.entity.User;
import com.example.springboot.repository.LoginLogRepository;
import com.example.springboot.repository.OperationLogRepository;
import com.example.springboot.repository.UserRepository;
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
    private LoginLogRepository loginLogRepository;

    @Resource
    private OperationLogRepository operationLogRepository;

    @Resource
    private UserRepository userRepository;

    @GetMapping
    public Result listLogs(@RequestParam(required = false) Integer limit) {
        try {
            List<Map<String, Object>> allLogs = new ArrayList<>();

            List<LoginLog> loginLogs = loginLogRepository.findAll();
            for (LoginLog log : loginLogs) {
                allLogs.add(toLogRowFromLoginLog(log));
            }

            List<OperationLog> operationLogs = operationLogRepository.findAll();
            for (OperationLog log : operationLogs) {
                allLogs.add(toLogRowFromOperationLog(log));
            }

            allLogs.sort((a, b) -> {
                Date da = (Date) a.get("time");
                Date db = (Date) b.get("time");
                if (da == null && db == null) return 0;
                if (da == null) return 1;
                if (db == null) return -1;
                return db.compareTo(da);
            });

            if (limit != null && limit > 0 && allLogs.size() > limit) {
                allLogs = allLogs.subList(0, limit);
            }

            return Result.success(allLogs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/summary")
    public Result logSummary() {
        try {
            List<LoginLog> loginLogs = loginLogRepository.findAll();
            List<OperationLog> operationLogs = operationLogRepository.findAll();

            int totalLogs = loginLogs.size() + operationLogs.size();

            LocalDate today = LocalDate.now();
            ZoneId zone = ZoneId.systemDefault();

            List<LoginLog> todayLoginLogs = loginLogs.stream().filter(l -> {
                Date d = l.getLoginTime();
                if (d == null) return false;
                LocalDate ld = d.toInstant().atZone(zone).toLocalDate();
                return ld.equals(today);
            }).collect(Collectors.toList());

            List<OperationLog> todayOperationLogs = operationLogs.stream().filter(l -> {
                Date d = l.getCreated_at();
                if (d == null) return false;
                LocalDate ld = d.toInstant().atZone(zone).toLocalDate();
                return ld.equals(today);
            }).collect(Collectors.toList());

            int todayLogs = todayLoginLogs.size() + todayOperationLogs.size();

            Map<Long, Integer> userOperationCount = new HashMap<>();
            for (OperationLog log : todayOperationLogs) {
                if (log.getUser_id() != null) {
                    userOperationCount.put(log.getUser_id(), userOperationCount.getOrDefault(log.getUser_id(), 0) + 1);
                }
            }

            Set<Long> activeUsers = new HashSet<>();
            for (LoginLog log : todayLoginLogs) {
                if (log.getUser_id() != null && userOperationCount.getOrDefault(log.getUser_id(), 0) > 0) {
                    activeUsers.add(log.getUser_id());
                }
            }

            long errorLogs = 0;
            for (LoginLog log : todayLoginLogs) {
                if (log.getStatus() != null && log.getStatus() != 1) {
                    errorLogs++;
                }
            }
            for (OperationLog log : todayOperationLogs) {
                if (log.getAction() != null && (log.getAction().contains("失败") || log.getAction().contains("错误"))) {
                    errorLogs++;
                }
            }

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

    private Map<String, Object> toLogRowFromLoginLog(LoginLog log) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", "login_" + log.getId());
        row.put("logType", "登录日志");
        row.put("time", log.getLoginTime());

        String userId = log.getUser_id() != null ? String.valueOf(log.getUser_id()) : "";
        String userName = "";
        if (log.getUser_id() != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                userName = userOpt.get().getName() != null ? userOpt.get().getName() : userId;
            } else {
                userName = userId;
            }
        }

        row.put("userId", userId);
        row.put("userName", userName);
        row.put("action", "登录系统");
        row.put("actionType", "login");
        row.put("ipAddress", log.getIp_address() != null ? log.getIp_address() : "");
        row.put("status", log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败");

        String created = formatDateTime(log.getLoginTime());
        row.put("loginTime", created);
        row.put("logoutTime", "");
        row.put("actionTime", created);
        row.put("submitCount", 0);
        row.put("teamId", "");
        row.put("teamName", "");

        return row;
    }

    private Map<String, Object> toLogRowFromOperationLog(OperationLog log) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", "operation_" + log.getId());
        row.put("logType", "操作日志");
        row.put("time", log.getCreated_at());

        String userId = log.getUser_id() != null ? String.valueOf(log.getUser_id()) : "";
        String userName = "";
        if (log.getUser_id() != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                userName = userOpt.get().getName() != null ? userOpt.get().getName() : userId;
            } else {
                userName = userId;
            }
        }

        row.put("userId", userId);
        row.put("userName", userName);
        row.put("action", log.getAction() != null ? log.getAction() : "");
        row.put("actionType", classifyActionType(log.getAction()));
        row.put("ipAddress", log.getIp_address() != null ? log.getIp_address() : "");
        row.put("status", "成功");

        String created = formatDateTime(log.getCreated_at());
        row.put("loginTime", "");
        row.put("logoutTime", "");
        row.put("actionTime", created);
        row.put("submitCount", 0);
        row.put("teamId", "");
        row.put("teamName", "");

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
