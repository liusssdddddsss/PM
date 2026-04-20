package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.AIWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/warnings")
@Tag(name="AI警告管理模块")
public class AIWarningController {

    @Autowired
    private AIWarningService aiWarningService;

    @Operation(summary = "获取所有AI警告", description = "返回所有AI警告列表")
    @GetMapping("/list")
    public Result getWarnings() {
        try {
            var result = aiWarningService.findAll();
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取AI警告失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据状态获取AI警告", description = "根据状态返回AI警告列表")
    @GetMapping("/list/status")
    public Result getWarningsByStatus(@RequestParam Integer status) {
        try {
            var result = aiWarningService.findByIsRead(status);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取AI警告失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据类型获取AI警告", description = "根据类型返回AI警告列表")
    @GetMapping("/list/type")
    public Result getWarningsByType(@RequestParam String type) {
        try {
            var result = aiWarningService.findByRiskType(type);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取AI警告失败: " + e.getMessage());
        }
    }

    @Operation(summary = "标记警告为已读", description = "将指定ID的警告标记为已读")
    @PostMapping("/read")
    public Result markAsRead(@RequestParam Long id) {
        try {
            aiWarningService.markAsRead(id);
            return Result.success("标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除警告", description = "删除指定ID的警告")
    @PostMapping("/delete")
    public Result deleteWarning(@RequestParam Long id) {
        try {
            aiWarningService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}