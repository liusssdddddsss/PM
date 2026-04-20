package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.AIAnalysisEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/engine")
@Tag(name="AI分析引擎模块")
public class AIAnalysisEngineController {

    @Autowired
    private AIAnalysisEngine aiAnalysisEngine;

    @Operation(summary = "获取AI分析引擎状态", description = "返回AI分析引擎的当前状态和扫描结果")
    @GetMapping("/status")
    public Result getEngineStatus() {
        try {
            Map<String, Object> status = aiAnalysisEngine.getEngineStatus();
            return Result.success(status);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取引擎状态失败: " + e.getMessage());
        }
    }

    @Operation(summary = "立即执行全量扫描", description = "手动触发AI分析引擎进行全量扫描")
    @PostMapping("/scan")
    public Result executeScan() {
        try {
            aiAnalysisEngine.executeScan();
            return Result.success("扫描任务已启动");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("启动扫描任务失败: " + e.getMessage());
        }
    }

    @Operation(summary = "扫描单个项目", description = "手动触发AI分析引擎扫描指定项目")
    @PostMapping("/scan/project")
    public Result scanProject(@RequestParam Long projectId) {
        try {
            aiAnalysisEngine.scanProject(projectId);
            return Result.success("项目扫描任务已启动");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("启动项目扫描任务失败: " + e.getMessage());
        }
    }

    @Operation(summary = "扫描单个产品", description = "手动触发AI分析引擎扫描指定产品")
    @PostMapping("/scan/product")
    public Result scanProduct(@RequestParam Long productId) {
        try {
            aiAnalysisEngine.scanProduct(productId);
            return Result.success("产品扫描任务已启动");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("启动产品扫描任务失败: " + e.getMessage());
        }
    }
}