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
import java.util.List;
import java.util.ArrayList;

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

    @Operation(summary = "获取团队完成情况数据", description = "返回团队的完成情况数据")
    @GetMapping("/team-statistics")
    public Result getTeamStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 模拟团队完成情况数据
            Map<String, Integer> yesterday = new HashMap<>();
            yesterday.put("task", 120);
            yesterday.put("create", 56);
            yesterday.put("tiChu", 159);
            yesterday.put("bug", 165);
            yesterday.put("clock", 65);
            yesterday.put("averageClock", 7);
            
            Map<String, Integer> today = new HashMap<>();
            today.put("task", 68);
            today.put("create", 45);
            today.put("tiChu", 123);
            today.put("bug", 158);
            today.put("clock", 50);
            today.put("averageClock", 5);
            
            statistics.put("yesterday", yesterday);
            statistics.put("today", today);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取团队完成情况失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品总览数据", description = "返回产品总览数据")
    @GetMapping("/product-overview")
    public Result getProductOverview() {
        try {
            Map<String, Integer> overview = new HashMap<>();
            
            // 模拟产品总览数据
            overview.put("projectCount", 24);
            overview.put("thisYearIssue", 23);
            overview.put("closeCount", 24);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品总览失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目总览数据", description = "返回项目总览数据")
    @GetMapping("/project-overview")
    public Result getProjectOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 模拟项目总览数据
            overview.put("xiangMuCount", 12);
            overview.put("thisYearFinish", 12);
            
            // 模拟项目年份分布数据
            Map<String, Object> projectYearsData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", new String[] {"2022", "2023", "2024"});
            projectYearsData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            projectYearsData.put("yAxis", yAxis);
            
            Map<String, Object> seriesItem = new HashMap<>();
            seriesItem.put("type", "bar");
            seriesItem.put("data", new int[] {20, 100, 30});
            projectYearsData.put("series", new Map[] {seriesItem});
            
            overview.put("projectYearsData", projectYearsData);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目总览失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取任务完成总览数据", description = "返回任务完成总览数据")
    @GetMapping("/task-overview")
    public Result getTaskOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 模拟任务完成总览数据
            overview.put("taskAllCount", 1222);
            overview.put("taskFinishCount", 1000);
            
            // 模拟任务分布数据
            Map<String, Object> taskDistributionData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", new String[] {"未开始", "进行中", "已排期"});
            taskDistributionData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            taskDistributionData.put("yAxis", yAxis);
            
            Map<String, Object> seriesItem = new HashMap<>();
            seriesItem.put("type", "bar");
            seriesItem.put("data", new int[] {20, 100, 30});
            taskDistributionData.put("series", new Map[] {seriesItem});
            
            overview.put("taskDistributionData", taskDistributionData);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取任务完成总览失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取需求统计数据", description = "返回需求统计数据")
    @GetMapping("/needs-statistics")
    public Result getNeedsStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 模拟需求统计数据
            Map<String, Object> needsChartData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", new String[] {"7月", "8月", "9月", "10月", "11月", "12月"});
            needsChartData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            needsChartData.put("yAxis", yAxis);
            
            Map<String, Object> seriesItem1 = new HashMap<>();
            seriesItem1.put("name", "finish");
            seriesItem1.put("type", "line");
            seriesItem1.put("stack", "Total");
            seriesItem1.put("data", new int[] {15, 24, 54, 23, 14, 56});
            
            Map<String, Object> seriesItem2 = new HashMap<>();
            seriesItem2.put("name", "add");
            seriesItem2.put("type", "line");
            seriesItem2.put("stack", "Total");
            seriesItem2.put("data", new int[] {35, 67, 34, 28, 89, 99});
            
            needsChartData.put("series", new Map[] {seriesItem1, seriesItem2});
            
            statistics.put("needsChartData", needsChartData);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取需求统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取测试统计数据", description = "返回测试统计数据")
    @GetMapping("/test-statistics")
    public Result getTestStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 模拟测试统计数据
            statistics.put("yesterdayNew", 125);
            statistics.put("todayNew", 65);
            statistics.put("yesterdaySolved", 98);
            statistics.put("todaySolved", 26);
            statistics.put("bugRepairRate", 50);
            statistics.put("validBugs", 56);
            statistics.put("fixedBugs", 16);
            statistics.put("unclosedBugs", 42);
            
            // 模拟待测试的测试单列表
            List<String> testLists = new ArrayList<>();
            testLists.add("班牌PC端管理界面调整");
            testLists.add("学期结束后，自动给学生推送实训档案");
            testLists.add("班牌模板调整，参考海康，增加竖版");
            testLists.add("家校互通留言");
            testLists.add("终端-教师端查询评分标准列表");
            statistics.put("testLists", testLists);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测试统计数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取最新动态数据", description = "返回最新动态数据")
    @GetMapping("/dynamic")
    public Result getDynamicData() {
        try {
            // 模拟最新动态数据
            List<Map<String, String>> dynamicData = new ArrayList<>();
            
            Map<String, String> item1 = new HashMap<>();
            item1.put("time", "08月08日 12:12");
            item1.put("operator", "张三三");
            item1.put("action", "审批了研发需求");
            item1.put("link", "班牌模板调整，参考海康，增加竖版");
            dynamicData.add(item1);
            
            Map<String, String> item2 = new HashMap<>();
            item2.put("time", "08月08日 12:12");
            item2.put("operator", "王麻子");
            item2.put("action", "确认通过研发需求");
            item2.put("link", "家校互通留言");
            dynamicData.add(item2);
            
            return Result.success(dynamicData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取最新动态失败: " + e.getMessage());
        }
    }
}