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
                result.add(toFrontendRow(item));
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询反馈失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/process")
    public Result processFeedback(@PathVariable Long id, @RequestBody AdminFeedbackProcessRequest req) {
        try {
            if (req == null) return Result.error("参数不能为空");

            Feedback feedback = feedbackRepository.findById(id).orElse(null);
            if (feedback == null) return Result.error("反馈不存在");

            // reply -> solution
            feedback.setSolution(req.reply);
            feedback.setStatus(mapStatusToBackend(req.status));
            feedback.setUpdatedAt(new Date());

            Feedback saved = feedbackRepository.save(feedback);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("处理反馈失败: " + e.getMessage());
        }
    }

    private Map<String, Object> toFrontendRow(FeedbackService.FeedbackWithAssigneeName item) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", item.getId());
        row.put("userId", item.getCreatorId() != null ? String.valueOf(item.getCreatorId()) : "");
        row.put("userName", item.getCreatorName() != null ? item.getCreatorName() : "");
        row.put("teamId", "");
        row.put("teamName", "");

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
        return "已处理";
    }

    private static String mapStatusToBackend(String frontendStatus) {
        if (frontendStatus == null) return "待处理";
        if ("处理中".equals(frontendStatus)) return "处理中";
        if ("已处理".equals(frontendStatus)) return "待关闭";
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

