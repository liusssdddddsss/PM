package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.BugService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.RequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Tag(name="工作台模块")
public class DashboardController {

    @Resource
    BugService bugService;
    @Resource
    ProjectApprovalService projectApprovalService;
    @Resource
    TaskService taskService;
    @Resource
    RequirementService requirementService;

    @Operation(summary = "获取工作台统计数据", description = "返回工作台的统计数据，包括待审批数、任务数、BUG数等")
    @GetMapping("/statistics")
    public Result getStatistics() {
        try {
            // 从数据库中获取真实数据
            Map<String, Integer> statistics = new HashMap<>();
            
            // 获取Bug总数
            long bugCount = bugService.count();
            
            // 获取待审批数
            long approveCount = projectApprovalService.count();
            
            // 获取任务数
            long taskCount = taskService.count();
            
            // 获取研发需求数（type = 1）
            long researchNeedsCount = requirementService.countByType(1);
            
            // 获取用户需求数（type = 2）
            long userNeedsCount = requirementService.countByType(2);
            
            // 其他统计数据暂时使用模拟数据，等其他表创建后再修改为真实查询
            statistics.put("approveState", (int) approveCount);  // 待审批数
            statistics.put("taskState", (int) taskCount);      // 任务数
            statistics.put("bugState", (int) bugCount);        // BUG数
            statistics.put("needsState", (int) researchNeedsCount);      // 研发需求数
            statistics.put("userState", (int) userNeedsCount);       // 用户需求数
            statistics.put("passageState", 10);    // 文档数
            statistics.put("bug", 10);             // 昨日处理任务和Bug数
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
}