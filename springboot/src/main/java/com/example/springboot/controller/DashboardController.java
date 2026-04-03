package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Dashboard", description = "Dashboard相关接口")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private TestSuiteService testSuiteService;

    @Autowired
    private BugService bugService;

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "获取测试统计数据", description = "返回测试相关的统计数据")
    @GetMapping("/test-statistics")
    public Result getTestStatistics() {
        try {
            // 从数据库获取测试套件数据
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<Bug> bugs = bugService.findall();

            // 计算统计数据
            int yesterdayNew = 0;
            int yesterdayResolved = 0;
            int todayNew = 0;
            int todayResolved = 0;
            int yesterdayClosed = 0;
            int todayClosed = 0;

            // 获取昨天和今天的日期
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            cal.add(Calendar.DATE, -1);
            Date yesterday = cal.getTime();

            // 统计Bug数据
            int validBugs = 0;
            int resolvedBugs = 0;
            int unresolvedBugs = 0;

            for (Bug bug : bugs) {
                if (bug.getStatus() != null) {
                    if (bug.getStatus() == 0 || bug.getStatus() == 1) {
                        validBugs++;
                        unresolvedBugs++;
                    } else if (bug.getStatus() == 2) {
                        validBugs++;
                        resolvedBugs++;
                    }
                }
            }

            // 构建返回数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("yesterdayNew", yesterdayNew);
            statistics.put("yesterdayResolved", yesterdayResolved);
            statistics.put("todayNew", todayNew);
            statistics.put("todayResolved", todayResolved);
            statistics.put("yesterdayClosed", yesterdayClosed);
            statistics.put("todayClosed", todayClosed);
            statistics.put("valid", validBugs);
            statistics.put("resolved", resolvedBugs);
            statistics.put("unresolved", unresolvedBugs);

            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测试统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户测试任务", description = "返回当前用户的测试任务列表")
    @GetMapping("/user-test-tasks")
    public Result getUserTestTasks(@RequestParam String username) {
        try {
            // 从数据库获取用户的测试任务
            // 注意：test_suites表中的assignee_id存储的是username，不是userId
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<Map<String, Object>> userTestTasks = new ArrayList<>();
            
            for (TestSuite testSuite : testSuites) {
                if (testSuite.getAssignee_id() != null && testSuite.getAssignee_id().toString().equals(username)) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("name", testSuite.getName());
                    
                    // 转换优先级
                    String priority = "一般";
                    if (testSuite.getPriority() != null) {
                        switch (testSuite.getPriority()) {
                            case 1: priority = "紧急"; break;
                            case 2: priority = "一般"; break;
                            case 3: priority = "正常"; break;
                        }
                    }
                    task.put("priority", priority);
                    
                    // 转换状态
                    String status = "待测试";
                    if (testSuite.getStatus() != null) {
                        switch (testSuite.getStatus()) {
                            case 1: status = "待测试"; break;
                            case 2: status = "测试中"; break;
                            case 3: status = "已完成"; break;
                            case 4: status = "已关闭"; break;
                        }
                    }
                    task.put("status", status);
                    
                    // 获取项目名称
                    String projectName = "未知项目";
                    try {
                        if (testSuite.getProject_id() != null) {
                            Optional<Project> projectOpt = projectService.findById(testSuite.getProject_id());
                            if (projectOpt.isPresent()) {
                                projectName = projectOpt.get().getName();
                            }
                        }
                    } catch (Exception e) {
                        // 如果获取项目名称失败，使用默认值
                    }
                    task.put("project", projectName);
                    
                    userTestTasks.add(task);
                }
            }
            
            return Result.success(userTestTasks);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户测试任务失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户Bug列表", description = "返回当前用户的Bug列表")
    @GetMapping("/user-bugs")
    public Result getUserBugs(@RequestParam String username) {
        try {
            // 从数据库获取用户的Bug列表
            // 注意：bugs表中的assignee_id存储的是username，不是userId
            List<Bug> bugs = bugService.findall();
            List<Map<String, Object>> userBugs = new ArrayList<>();
            
            for (Bug bug : bugs) {
                if (bug.getAssignee_id() != null && bug.getAssignee_id().toString().equals(username)) {
                    Map<String, Object> bugMap = new HashMap<>();
                    bugMap.put("name", bug.getTitle() != null ? bug.getTitle() : "无标题");
                    
                    // 转换优先级（使用severity字段）
                    String priority = "一般";
                    if (bug.getSeverity() != null) {
                        switch (bug.getSeverity()) {
                            case 1: priority = "严重"; break;
                            case 2: priority = "一般"; break;
                            case 3: priority = "正常"; break;
                        }
                    }
                    bugMap.put("priority", priority);
                    
                    // 转换状态
                    String status = "待处理";
                    if (bug.getStatus() != null) {
                        switch (bug.getStatus()) {
                            case 0: status = "待处理"; break;
                            case 1: status = "解决中"; break;
                            case 2: status = "已解决"; break;
                        }
                    }
                    bugMap.put("status", status);
                    
                    userBugs.add(bugMap);
                }
            }
            
            return Result.success(userBugs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户Bug列表失败: " + e.getMessage());
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
    
    @Operation(summary = "获取测试用例列表", description = "返回所有测试用例列表")
    @GetMapping("/test-cases")
    public Result getTestCases() {
        try {
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<Map<String, Object>> testCases = new ArrayList<>();
            
            for (TestSuite testSuite : testSuites) {
                Map<String, Object> testCase = new HashMap<>();
                testCase.put("id", testSuite.getId());
                testCase.put("name", testSuite.getName());
                testCase.put("title", testSuite.getName());
                
                // 获取项目名称
                String projectName = "未知项目";
                if (testSuite.getProject_id() != null) {
                    Optional<Project> projectOpt = projectService.findById(testSuite.getProject_id());
                    if (projectOpt.isPresent()) {
                        projectName = projectOpt.get().getName();
                    }
                }
                testCase.put("project_name", projectName);
                
                // 转换优先级
                testCase.put("priority", testSuite.getPriority() != null ? testSuite.getPriority() : 3);
                
                // 转换状态 - status是Integer类型，直接传递
                testCase.put("status", testSuite.getStatus() != null ? testSuite.getStatus() : 1);
                
                // 截止时间
                testCase.put("due_date", testSuite.getEnd_date() != null ? testSuite.getEnd_date().toString() : "");
                
                // 进度
                testCase.put("progress", 0);
                
                testCases.add(testCase);
            }
            
            return Result.success(testCases);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测试用例列表失败: " + e.getMessage());
        }
    }
}
