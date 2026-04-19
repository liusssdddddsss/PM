package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.AIAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/analysis")
@Tag(name="AI分析预警模块")
public class AIAnalysisController {

    @Autowired
    private AIAnalysisService aiAnalysisService;

    @Operation(summary = "分析所有项目的风险", description = "返回所有项目的风险分析数据")
    @GetMapping("/projects")
    public Result analyzeProjectsRisk() {
        try {
            var result = aiAnalysisService.analyzeProjectsRisk();
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("分析项目风险失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分析所有产品的风险", description = "返回所有产品的风险分析数据")
    @GetMapping("/products")
    public Result analyzeProductsRisk() {
        try {
            var result = aiAnalysisService.analyzeProductsRisk();
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("分析产品风险失败: " + e.getMessage());
        }
    }
}
