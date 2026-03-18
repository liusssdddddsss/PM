package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Feedback;
import com.example.springboot.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    // 获取反馈列表
    @GetMapping("/list")
    public Result getFeedbackList() {
        try {
            List<FeedbackService.FeedbackWithAssigneeName> feedbacks = feedbackService.findAll();
            return Result.success(feedbacks);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取反馈列表失败: " + e.getMessage());
        }
    }

    // 创建反馈
    @PostMapping("/create")
    public Result createFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback savedFeedback = feedbackService.save(feedback);
            return Result.success(savedFeedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建反馈失败: " + e.getMessage());
        }
    }

    // 更新反馈
    @PutMapping("/update")
    public Result updateFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback updatedFeedback = feedbackService.save(feedback);
            return Result.success(updatedFeedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新反馈失败: " + e.getMessage());
        }
    }

    // 删除反馈
    @DeleteMapping("/delete/{id}")
    public Result deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除反馈失败: " + e.getMessage());
        }
    }

    // 获取单个反馈
    @GetMapping("/detail/{id}")
    public Result getFeedbackDetail(@PathVariable Long id) {
        try {
            FeedbackService.FeedbackWithAssigneeName feedback = feedbackService.findById(id);
            return Result.success(feedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取反馈详情失败: " + e.getMessage());
        }
    }
}
