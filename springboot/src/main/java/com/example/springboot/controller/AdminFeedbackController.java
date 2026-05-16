package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Feedback;
import com.example.springboot.repository.FeedbackRepository;
import com.example.springboot.service.FeedbackService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @Resource
    private FeedbackRepository feedbackRepository;

    @GetMapping
    public Result listFeedbacks() {
        try {
            List<FeedbackService.FeedbackWithAssigneeName> feedbacks = feedbackService.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (FeedbackService.FeedbackWithAssigneeName item : feedbacks) {
                Map<String, Object> row = toFrontendRow(item);
                System.out.println("反馈ID: " + item.getId() + ", 后端状态: " + item.getStatus() + ", 前端状态: " + row.get("status"));
                result.add(row);
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询反馈失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/process")
    public Result processFeedback(@PathVariable Long id, @RequestBody AdminFeedbackProcessRequest req) {
        try {
            System.out.println("处理反馈请求 - ID: " + id + ", 状态: " + (req != null ? req.status : "null") + ", 回复: " + (req != null ? req.reply : "null"));
            if (req == null) return Result.error("参数不能为空");

            Feedback feedback = feedbackRepository.findById(id).orElse(null);
            if (feedback == null) return Result.error("反馈不存在");
            
            System.out.println("找到反馈 - 原状态: " + feedback.getStatus());

            // reply -> solution
            feedback.setSolution(req.reply);
            String newStatus = mapStatusToBackend(req.status);
            feedback.setStatus(newStatus);
            feedback.setUpdatedAt(new Date());
            
            System.out.println("设置反馈 - 新状态: " + newStatus);

            Feedback saved = feedbackRepository.save(feedback);
            System.out.println("保存反馈成功 - 最终状态: " + saved.getStatus());
            return Result.success(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理反馈失败: " + e.getMessage());
        }
    }

    private Map<String, Object> toFrontendRow(FeedbackService.FeedbackWithAssigneeName item) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", item.getId());
        row.put("userId", item.getCreatorId() != null ? String.valueOf(item.getCreatorId()) : "");
        row.put("userName", item.getCreatorName() != null ? item.getCreatorName() : "");
        row.put("teamId", item.getTeamId() != null ? String.valueOf(item.getTeamId()) : "");
        row.put("teamName", item.getTeamName() != null ? item.getTeamName() : "");

        row.put("title", item.getTitle());
        row.put("type", item.getType());
        row.put("content", item.getDescription());
        row.put("submitTime", formatDateTime(item.getCreatedAt()));
        row.put("status", mapStatusToFrontend(item.getStatus()));
        row.put("reply", item.getSolution());
        row.put("attachments", Collections.emptyList());
        return row;
    }

    private static String mapStatusToFrontend(String backendStatus) {
        if ("处理中".equals(backendStatus)) return "处理中";
        if ("待处理".equals(backendStatus)) return "未处理";
        if ("已处理".equals(backendStatus)) return "已处理";
        if ("已完成".equals(backendStatus)) return "已完成";
        if ("待关闭".equals(backendStatus)) return "已处理";
        return "未处理";
    }

    private static String mapStatusToBackend(String frontendStatus) {
        if (frontendStatus == null) return "待处理";
        if ("处理中".equals(frontendStatus)) return "处理中";
        if ("已处理".equals(frontendStatus)) return "已处理";
        if ("已完成".equals(frontendStatus)) return "已完成";
        if ("未处理".equals(frontendStatus)) return "待处理";
        return "待处理";
    }

    private static String formatDateTime(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }

    public static class AdminFeedbackProcessRequest {
        public String reply;
        public String status; // 未处理/处理中/已处理
    }
}

