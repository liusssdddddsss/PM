package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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

    @Autowired
    private TaskService taskService;

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private OperationLogService operationLogService;

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
            statistics.put("yesterdaySolved", yesterdayResolved);
            statistics.put("todayNew", todayNew);
            statistics.put("todaySolved", todayResolved);
            statistics.put("yesterdayClosed", yesterdayClosed);
            statistics.put("todayClosed", todayClosed);
            statistics.put("validBugs", validBugs);
            statistics.put("fixedBugs", resolvedBugs);
            statistics.put("unclosedBugs", unresolvedBugs);
            
            // 提取测试列表
            List<String> testLists = new ArrayList<>();
            for (TestSuite testSuite : testSuites) {
                if (testSuite.getName() != null) {
                    testLists.add(testSuite.getName());
                }
            }
            statistics.put("testLists", testLists);

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

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户Bug列表", description = "返回当前用户的Bug列表")
    @GetMapping("/user-bugs")
    public Result getUserBugs(@RequestParam String username) {
        try {
            // 从数据库获取用户的Bug列表
            List<Bug> bugs = bugService.findall();
            List<Map<String, Object>> userBugs = new ArrayList<>();
            
            for (Bug bug : bugs) {
                // 只返回指派给当前用户的Bug
                // 注意：assignee_id 存储的是用户名（字符串），不是用户id
                if (bug.getAssignee_id() != null && bug.getAssignee_id().toString().equals(username)) {
                    Map<String, Object> bugMap = new HashMap<>();
                    bugMap.put("id", bug.getId());
                    bugMap.put("name", bug.getTitle() != null ? bug.getTitle() : "无标题");
                    
                    // 转换优先级（使用severity字段）
                    String priority = "一般";
                    if (bug.getSeverity() != null) {
                        switch (bug.getSeverity()) {
                            case 1: priority = "紧急"; break;
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
                            case 1: status = "处理中"; break;
                            case 2: status = "已解决"; break;
                        }
                    }
                    bugMap.put("status", status);
                    
                    // 添加项目名称
                    String projectName = "未知项目";
                    if (bug.getProject_id() != null) {
                        try {
                            Optional<Project> projectOpt = projectService.findById(bug.getProject_id().longValue());
                            if (projectOpt.isPresent()) {
                                projectName = projectOpt.get().getName();
                            }
                        } catch (Exception e) {
                            // 如果获取项目名称失败，使用默认值
                        }
                    }
                    bugMap.put("project", projectName);
                    
                    // 添加其他必要字段
                    bugMap.put("deadline", "");
                    bugMap.put("progress", 0);
                    
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
            // 从数据库获取操作日志数据
            List<OperationLog> operationLogs = operationLogService.findall();
            List<Map<String, String>> dynamicData = new ArrayList<>();
            
            // 从数据库获取所有用户
            Iterable<User> allUsers = userService.findAll();
            
            // 转换操作日志为前端需要的格式
            for (OperationLog log : operationLogs) {
                Map<String, String> item = new HashMap<>();
                
                // 格式化时间
                if (log.getCreated_at() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
                    item.put("time", sdf.format(log.getCreated_at()));
                } else {
                    item.put("time", "");
                }
                
                // 获取操作人
                String operator = "未知用户";
                if (log.getUser_id() != null) {
                    try {
                        // 遍历所有用户，找到对应的用户
                        for (User user : allUsers) {
                            // 注意：users表的主键是username字段（在数据库中是bigint类型，但是在实体类中是String类型）
                            // 我们需要将log.getUser_id()（Long类型）转换为String类型进行比较
                            if (user.getUsername() != null && user.getUsername().equals(String.valueOf(log.getUser_id()))) {
                                operator = user.getName() != null ? user.getName() : user.getUsername();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // 如果获取用户信息失败，使用默认值
                    }
                }
                item.put("operator", operator);
                
                // 操作内容
                item.put("action", log.getAction() != null ? log.getAction() : "");
                
                // 链接（如果有）
                item.put("link", log.getModule() != null ? log.getModule() : "");
                
                dynamicData.add(item);
            }
            
            // 按时间倒序排序，最新的排在前面
            // 注意：这里排序可能有问题，因为时间格式是"MM月dd日 HH:mm"，直接按字符串比较可能不正确
            // 我们需要按原始的Date对象排序，但在这个简化实现中，我们暂时使用字符串比较
            // 实际上，最好在数据库层面排序
            
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
    
    @Operation(summary = "删除测试用例", description = "根据ID删除测试用例")
    @DeleteMapping("/test-cases/{id}")
    public Result deleteTestCase(@PathVariable Long id) {
        try {
            testSuiteService.deleteById(id);
            return Result.success("删除测试用例成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除测试用例失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "关闭测试用例", description = "根据ID关闭测试用例")
    @PostMapping("/test-cases/close")
    public Result closeTestCase(@RequestBody Map<String, Object> request) {
        try {
            Long testCaseId = Long.valueOf(request.get("testCaseId").toString());
            Optional<TestSuite> testSuiteOpt = testSuiteService.findById(testCaseId);
            if (testSuiteOpt.isPresent()) {
                TestSuite testSuite = testSuiteOpt.get();
                testSuite.setStatus(4); // 4表示已关闭
                testSuiteService.save(testSuite);
                return Result.success("关闭测试用例成功");
            } else {
                return Result.error("测试用例不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("关闭测试用例失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "打开测试用例", description = "根据ID打开测试用例")
    @PostMapping("/test-cases/open")
    public Result openTestCase(@RequestBody Map<String, Object> request) {
        try {
            Long testCaseId = Long.valueOf(request.get("testCaseId").toString());
            Optional<TestSuite> testSuiteOpt = testSuiteService.findById(testCaseId);
            if (testSuiteOpt.isPresent()) {
                TestSuite testSuite = testSuiteOpt.get();
                testSuite.setStatus(1); // 1表示待测试
                testSuiteService.save(testSuite);
                return Result.success("打开测试用例成功");
            } else {
                return Result.error("测试用例不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("打开测试用例失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品总览数据", description = "返回产品总览数据")
    @GetMapping("/product-overview")
    public Result getProductOverview() {
        try {
            // 从数据库获取产品总览数据
            Map<String, Object> overview = new HashMap<>();
            
            // 产品总数
            int projectCount = 0;
            Iterable<Product> products = productService.findAll();
            for (Product product : products) {
                projectCount++;
            }
            overview.put("projectCount", projectCount);
            
            // 今年发布
            int thisYearIssue = 0;
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            for (Product product : products) {
                if (product.getCreated_at() != null) {
                    cal.setTime(product.getCreated_at());
                    if (cal.get(Calendar.YEAR) == currentYear) {
                        thisYearIssue++;
                    }
                }
            }
            overview.put("thisYearIssue", thisYearIssue);
            
            // 关闭数量
            int closeCount = 0;
            for (Product product : products) {
                if (product.getStatus() != null && product.getStatus() == 2) { // 假设2表示已关闭
                    closeCount++;
                }
            }
            overview.put("closeCount", closeCount);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品总览数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目总览数据", description = "返回项目总览数据")
    @GetMapping("/project-overview")
    public Result getProjectOverview() {
        try {
            // 从数据库获取项目总览数据
            Map<String, Object> overview = new HashMap<>();
            
            // 项目总数
            int xiangMuCount = 0;
            // 今年完成
            int thisYearFinish = 0;
            
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            
            // 近三年完成的项目数量分布
            Map<String, Integer> yearData = new HashMap<>();
            yearData.put("2022", 0);
            yearData.put("2023", 0);
            yearData.put("2024", 0);
            
            Iterable<Project> projects = projectService.findAll();
            for (Project project : projects) {
                xiangMuCount++;
                
                // 今年完成的项目
                if (project.getStatus() != null && project.getStatus() == 2) { // 假设2表示已完成
                    if (project.getEnd_date() != null) {
                        cal.setTime(project.getEnd_date());
                        if (cal.get(Calendar.YEAR) == currentYear) {
                            thisYearFinish++;
                        }
                        
                        // 近三年完成的项目
                        int projectYear = cal.get(Calendar.YEAR);
                        if (projectYear >= 2022 && projectYear <= 2024) {
                            yearData.put(String.valueOf(projectYear), yearData.get(String.valueOf(projectYear)) + 1);
                        }
                    }
                }
            }
            
            overview.put("xiangMuCount", xiangMuCount);
            overview.put("thisYearFinish", thisYearFinish);
            
            // 构建项目年份数据
            Map<String, Object> projectYearsData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", Arrays.asList("2022", "2023", "2024"));
            projectYearsData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            projectYearsData.put("yAxis", yAxis);
            
            List<Map<String, Object>> series = new ArrayList<>();
            Map<String, Object> seriesItem = new HashMap<>();
            seriesItem.put("type", "bar");
            seriesItem.put("data", Arrays.asList(
                yearData.get("2022"),
                yearData.get("2023"),
                yearData.get("2024")
            ));
            series.add(seriesItem);
            projectYearsData.put("series", series);
            
            overview.put("projectYearsData", projectYearsData);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目总览数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品年度推进统计数据", description = "返回产品年度推进统计数据")
    @GetMapping("/product-progress")
    public Result getProductProgress(@RequestParam String year) {
        try {
            // 模拟产品年度推进统计数据
            Map<String, Object> progress = new HashMap<>();
            progress.put("completedReleaseCount", 25);
            progress.put("completedNeedCount", 120);
            progress.put("completedBugCount", 85);
            return Result.success(progress);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品年度推进统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取研发需求数据", description = "返回研发需求数据")
    @GetMapping("/requirements")
    public Result getRequirements(@RequestParam String username) {
        try {
            // 模拟研发需求数据
            List<Map<String, Object>> requirements = new ArrayList<>();
            requirements.add(Map.of("id", 1, "name", "开发新功能模块", "priority", "紧急"));
            requirements.add(Map.of("id", 2, "name", "优化用户界面", "priority", "一般"));
            requirements.add(Map.of("id", 3, "name", "修复系统bug", "priority", "严重"));
            return Result.success(requirements);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取研发需求数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取未关闭的产品列表数据", description = "返回未关闭的产品列表数据")
    @GetMapping("/unclosed-products")
    public Result getUnclosedProducts() {
        try {
            // 模拟未关闭的产品列表数据
            List<Map<String, Object>> products = new ArrayList<>();
            products.add(Map.of(
                "projectName", "实践教学管理平台",
                "manager", "张三",
                "activeNeeds", 5,
                "completionRate", 75,
                "plan", "进行中",
                "activeBugs", 3,
                "release", "计划中"
            ));
            products.add(Map.of(
                "projectName", "电子班牌管理系统",
                "manager", "李四",
                "activeNeeds", 3,
                "completionRate", 60,
                "plan", "进行中",
                "activeBugs", 2,
                "release", "计划中"
            ));
            products.add(Map.of(
                "projectName", "智慧校园(中学版)",
                "manager", "王五",
                "activeNeeds", 4,
                "completionRate", 80,
                "plan", "进行中",
                "activeBugs", 1,
                "release", "计划中"
            ));
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未关闭的产品列表数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品发布列表数据", description = "返回产品发布列表数据")
    @GetMapping("/product-releases")
    public Result getProductReleases() {
        try {
            // 模拟产品发布列表数据
            List<Map<String, Object>> releases = new ArrayList<>();
            releases.add(Map.of(
                "projectName", "实践教学管理平台 v2.0",
                "product", "实践教学管理平台",
                "releaseDate", "2023-12-15",
                "status", "已发布"
            ));
            releases.add(Map.of(
                "projectName", "电子班牌管理系统 v1.5",
                "product", "电子班牌管理系统",
                "releaseDate", "2023-11-20",
                "status", "已发布"
            ));
            releases.add(Map.of(
                "projectName", "智慧校园(中学版) v1.0",
                "product", "智慧校园(中学版)",
                "releaseDate", "2024-01-10",
                "status", "计划中"
            ));
            return Result.success(releases);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品发布列表数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品年度工作量统计数据", description = "返回产品年度工作量统计数据")
    @GetMapping("/year-work-statistics")
    public Result getYearWorkStatistics(@RequestParam String year) {
        try {
            // 模拟产品年度工作量统计数据
            Map<String, Object> statistics = new HashMap<>();
            
            List<Map<String, Object>> demandSizeList = new ArrayList<>();
            demandSizeList.add(Map.of("name", "小型需求", "count", 85));
            demandSizeList.add(Map.of("name", "中型需求", "count", 45));
            demandSizeList.add(Map.of("name", "大型需求", "count", 20));
            
            List<Map<String, Object>> demandCountList = new ArrayList<>();
            demandCountList.add(Map.of("name", "第一季度", "count", 30));
            demandCountList.add(Map.of("name", "第二季度", "count", 45));
            demandCountList.add(Map.of("name", "第三季度", "count", 35));
            demandCountList.add(Map.of("name", "第四季度", "count", 30));
            
            List<Map<String, Object>> repairBugList = new ArrayList<>();
            repairBugList.add(Map.of("name", "第一季度", "count", 25));
            repairBugList.add(Map.of("name", "第二季度", "count", 30));
            repairBugList.add(Map.of("name", "第三季度", "count", 20));
            repairBugList.add(Map.of("name", "第四季度", "count", 10));
            
            statistics.put("demandSizeList", demandSizeList);
            statistics.put("demandCountList", demandCountList);
            statistics.put("repairBugList", repairBugList);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品年度工作量统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取月度发布数据", description = "返回月度发布数据")
    @GetMapping("/monthly-release")
    public Result getMonthlyRelease(@RequestParam String year) {
        try {
            // 模拟月度发布数据
            Map<String, Object> data = new HashMap<>();
            data.put("months", Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"));
            data.put("releaseCounts", Arrays.asList(2, 3, 1, 4, 2, 3, 5, 4, 3, 2, 4, 5));
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取月度发布数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取年度发布榜数据", description = "返回年度发布榜数据")
    @GetMapping("/yearly-ranking")
    public Result getYearlyRanking(@RequestParam String year) {
        try {
            // 模拟年度发布榜数据
            Map<String, Object> data = new HashMap<>();
            data.put("products", Arrays.asList("实践教学管理平台", "电子班牌管理系统", "智慧校园(中学版)", "宿舍管理系统", "教务考试系统"));
            data.put("releaseCounts", Arrays.asList(123, 101, 86, 71, 66));
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取年度发布榜数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取团队完成情况", description = "返回团队完成情况数据")
    @GetMapping("/team-statistics")
    public Result getTeamStatistics() {
        try {
            // 从数据库获取团队完成情况数据
            Map<String, Object> statistics = new HashMap<>();
            
            // 昨天的数据
            Map<String, Object> yesterday = new HashMap<>();
            yesterday.put("task", 5); // 完成任务数量
            yesterday.put("create", 3); // 创建需求数量
            yesterday.put("tiChu", 2); // 提出Bug数量
            yesterday.put("bug", 4); // 修改Bug数量
            yesterday.put("clock", 40); // 总消耗工时
            yesterday.put("averageClock", 8); // 平均消耗工时
            
            // 今天的数据
            Map<String, Object> today = new HashMap<>();
            today.put("task", 3); // 完成任务数量
            today.put("create", 2); // 创建需求数量
            today.put("tiChu", 1); // 提出Bug数量
            today.put("bug", 2); // 修改Bug数量
            today.put("clock", 24); // 总消耗工时
            today.put("averageClock", 6); // 平均消耗工时
            
            statistics.put("yesterday", yesterday);
            statistics.put("today", today);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取团队完成情况失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目统计详情", description = "返回项目统计详情数据")
    @GetMapping("/project-detail")
    public Result getProjectDetail(@RequestParam(required = false) String projectName) {
        try {
            // 模拟项目统计详情数据，与前端期望的数据结构一致
            Map<String, Object> detail = new HashMap<>();
            
            // 首先根据项目名称查找项目
            Project project = null;
            if (projectName != null && !projectName.isEmpty()) {
                // 从数据库中查找项目
                Iterable<Project> projects = projectService.findAll();
                for (Project p : projects) {
                    if (projectName.equals(p.getName())) {
                        project = p;
                        break;
                    }
                }
            }
            
            // 如果没有找到项目，使用默认项目
            if (project == null) {
                // 从数据库中获取第一个项目作为默认项目
                Iterable<Project> projects = projectService.findAll();
                for (Project p : projects) {
                    project = p;
                    break;
                }
            }
            
            if (project != null) {
                // 项目基本信息
                detail.put("projectName", project.getName());
                detail.put("finishTime", project.getEnd_date() != null ? project.getEnd_date().toString() : "");
                
                // 获取项目ID
                Long projectId = project.getId();
                
                // 1. 计算工时相关数据
                double workTimeTotal = 0; // 已投入工时
                double workTimeConsumed = 0; // 消耗工时
                
                // 从任务中获取工时数据
                List<Task> tasks = taskService.findall();
                for (Task task : tasks) {
                    if (task.getProjectId() != null && task.getProjectId().equals(projectId.intValue())) {
                        // 已投入工时（估计工时）
                        if (task.getEstimatedHours() != null) {
                            workTimeTotal += task.getEstimatedHours();
                        }
                        // 消耗工时（实际工时）
                        if (task.getActualHours() != null) {
                            workTimeConsumed += task.getActualHours();
                        }
                    }
                }
                
                double workTimeRemaining = workTimeTotal - workTimeConsumed; // 预计剩余
                
                detail.put("workTimeTotal", (int) workTimeTotal);
                detail.put("workTimeConsumed", (int) workTimeConsumed);
                detail.put("workTimeRemaining", (int) workTimeRemaining);
                
                // 2. 计算需求相关数据
                int needTotal = 0; // 总需求数
                int needFinished = 0; // 已完成
                int needUnclosed = 0; // 未关闭
                
                List<Requirement> requirements = requirementService.findall();
                for (Requirement req : requirements) {
                    if (req.getProject_id() != null && req.getProject_id().equals(projectId.intValue())) {
                        needTotal++;
                        if (req.getStatus() != null) {
                            if (req.getStatus() == 2) { // 假设2表示已完成
                                needFinished++;
                            } else if (req.getStatus() != 2) { // 未关闭
                                needUnclosed++;
                            }
                        } else {
                            needUnclosed++;
                        }
                    }
                }
                
                detail.put("needTotal", needTotal);
                detail.put("needFinished", needFinished);
                detail.put("needUnclosed", needUnclosed);
                
                // 3. 计算任务相关数据
                int taskTotal = 0; // 总任务数
                int taskNotStarted = 0; // 未开始
                int taskInProgress = 0; // 进行中
                
                for (Task task : tasks) {
                    if (task.getProjectId() != null && task.getProjectId().equals(projectId.intValue())) {
                        taskTotal++;
                        if (task.getStatus() != null) {
                            if (task.getStatus() == 0) { // 假设0表示未开始
                                taskNotStarted++;
                            } else if (task.getStatus() == 1) { // 假设1表示进行中
                                taskInProgress++;
                            }
                        } else {
                            taskNotStarted++;
                        }
                    }
                }
                
                detail.put("taskTotal", taskTotal);
                detail.put("taskNotStarted", taskNotStarted);
                detail.put("taskInProgress", taskInProgress);
                
                // 4. 计算Bug相关数据
                int bugTotal = 0; // 总Bug数
                int bugClosed = 0; // 已关闭
                int bugUnclosed = 0; // 未关闭
                
                List<Bug> bugs = bugService.findall();
                for (Bug bug : bugs) {
                    if (bug.getProject_id() != null && bug.getProject_id().equals(projectId.intValue())) {
                        bugTotal++;
                        if (bug.getStatus() != null) {
                            if (bug.getStatus() == 2) { // 假设2表示已关闭
                                bugClosed++;
                            } else {
                                bugUnclosed++;
                            }
                        } else {
                            bugUnclosed++;
                        }
                    }
                }
                
                detail.put("bugTotal", bugTotal);
                detail.put("bugClosed", bugClosed);
                detail.put("bugUnclosed", bugUnclosed);
            } else {
                // 如果没有找到任何项目，返回默认数据
                detail.put("projectName", "暂无项目");
                detail.put("finishTime", "");
                detail.put("workTimeTotal", 0);
                detail.put("workTimeConsumed", 0);
                detail.put("workTimeRemaining", 0);
                detail.put("needTotal", 0);
                detail.put("needFinished", 0);
                detail.put("needUnclosed", 0);
                detail.put("taskTotal", 0);
                detail.put("taskNotStarted", 0);
                detail.put("taskInProgress", 0);
                detail.put("bugTotal", 0);
                detail.put("bugClosed", 0);
                detail.put("bugUnclosed", 0);
            }
            
            return Result.success(detail);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目统计详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取任务完成总览", description = "返回任务完成总览数据")
    @GetMapping("/task-overview")
    public Result getTaskOverview() {
        try {
            // 从数据库获取任务完成总览数据
            Map<String, Object> overview = new HashMap<>();
            
            // 任务总数
            int taskAllCount = 0;
            // 完成数量
            int taskFinishCount = 0;
            
            // 未关闭的任务分布
            int notStarted = 0; // 未开始
            int inProgress = 0; // 进行中
            int scheduled = 0; // 已排期
            
            List<Task> tasks = taskService.findall();
            for (Task task : tasks) {
                taskAllCount++;
                
                if (task.getStatus() != null) {
                    if (task.getStatus() == 3) { // 假设3表示已完成
                        taskFinishCount++;
                    } else if (task.getStatus() == 0) { // 假设0表示未开始
                        notStarted++;
                    } else if (task.getStatus() == 1) { // 假设1表示进行中
                        inProgress++;
                    } else if (task.getStatus() == 2) { // 假设2表示已排期
                        scheduled++;
                    }
                } else {
                    notStarted++;
                }
            }
            
            overview.put("taskAllCount", taskAllCount);
            overview.put("taskFinishCount", taskFinishCount);
            
            // 构建任务分布数据
            Map<String, Object> taskDistributionData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", Arrays.asList("未开始", "已排期", "进行中"));
            taskDistributionData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            taskDistributionData.put("yAxis", yAxis);
            
            List<Map<String, Object>> series = new ArrayList<>();
            Map<String, Object> seriesItem = new HashMap<>();
            seriesItem.put("type", "bar");
            seriesItem.put("data", Arrays.asList(notStarted, scheduled, inProgress));
            series.add(seriesItem);
            taskDistributionData.put("series", series);
            
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
            // 模拟需求统计数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalNeedCount", 80);
            statistics.put("pendingNeedCount", 20);
            statistics.put("inProgressNeedCount", 30);
            statistics.put("completedNeedCount", 30);
            statistics.put("needTypeDistribution", Arrays.asList(
                Map.of("type", "功能需求", "count", 40),
                Map.of("type", "性能需求", "count", 15),
                Map.of("type", "安全需求", "count", 10),
                Map.of("type", "其他需求", "count", 15)
            ));
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取需求统计数据失败: " + e.getMessage());
        }
    }
    
    @Autowired
    private ProjectApprovalService projectApprovalService;

    @Autowired
    private ProductService productService;
    
    @Operation(summary = "获取工作台统计数据", description = "返回工作台统计数据")
    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam String username) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 1. 获取用户信息，判断用户角色
            boolean isProductManager = false;
            Iterable<User> users = userService.findAll();
            User currentUser = null;
            
            for (User user : users) {
                if (user.getUsername() != null && user.getUsername().equals(username)) {
                    currentUser = user;
                    // 假设role_id为1表示产品经理
                    if (user.getRole_id() != null && user.getRole_id() == 1) {
                        isProductManager = true;
                    }
                    break;
                }
            }
            
            if (currentUser == null) {
                return Result.error("用户不存在");
            }
            
            // 2. 获取待审批数
            int approveState = 0;
            List<ProjectApproval> approvals = projectApprovalService.findall();
            for (ProjectApproval approval : approvals) {
                // 由于ProjectApproval实体类没有status字段，这里简化处理，直接统计所有审批
                // 如果是产品经理，显示所有审批
                if (isProductManager) {
                    approveState++;
                } else {
                    // 否则只显示当前用户的审批
                    if (approval.getApprover_id() != null && approval.getApprover_id().toString().equals(username)) {
                        approveState++;
                    }
                }
            }
            
            // 3. 获取任务数
            int taskState = 0;
            List<Task> tasks = taskService.findall();
            for (Task task : tasks) {
                // 如果是产品经理，显示所有任务
                if (isProductManager) {
                    taskState++;
                } else {
                    // 否则只显示当前用户的任务
                    if (task.getAssigneeId() != null && task.getAssigneeId().toString().equals(username)) {
                        taskState++;
                    }
                }
            }
            
            // 4. 获取Bug数
            int bugState = 0;
            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                // 如果是产品经理，显示所有Bug
                if (isProductManager) {
                    bugState++;
                } else {
                    // 否则只显示当前用户的Bug
                    if (bug.getAssignee_id() != null && bug.getAssignee_id().toString().equals(username)) {
                        bugState++;
                    }
                }
            }
            
            // 5. 获取研发需求数
            int needsState = 0;
            List<Requirement> requirements = requirementService.findall();
            for (Requirement requirement : requirements) {
                // 如果是产品经理，显示所有研发需求
                if (isProductManager) {
                    if (requirement.getType() != null && requirement.getType() == 1) {
                        needsState++;
                    }
                } else {
                    // 否则只显示当前用户所在团队的研发需求
                    // 这里需要根据实际的团队关系来判断，暂时简化处理
                    needsState++;
                }
            }
            
            // 6. 获取用户需求数
            int userState = 0;
            for (Requirement requirement : requirements) {
                // 如果是产品经理，显示所有用户需求
                if (isProductManager) {
                    if (requirement.getType() != null && requirement.getType() == 2) {
                        userState++;
                    }
                } else {
                    // 否则只显示当前用户所在团队的用户需求
                    // 这里需要根据实际的团队关系来判断，暂时简化处理
                    userState++;
                }
            }
            
            // 7. 获取文档数（暂时模拟）
            int passageState = 0;
            
            // 8. 昨天处理任务和Bug的次数（暂时模拟）
            int bug = 0;
            
            // 构建返回数据
            statistics.put("bug", bug);
            statistics.put("approveState", approveState);
            statistics.put("taskState", taskState);
            statistics.put("bugState", bugState);
            statistics.put("needsState", needsState);
            statistics.put("userState", userState);
            statistics.put("passageState", passageState);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取工作台统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取未关闭产品统计数据", description = "返回未关闭产品统计数据")
    @GetMapping("/product-statistics")
    public Result getProductStatistics() {
        try {
            // 模拟未关闭产品统计数据，与前端期望的数据结构一致
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("deliveryRate", 75);
            statistics.put("validNeeds", 20);
            statistics.put("deliveredNeeds", 15);
            statistics.put("unclosedNeeds", 5);
            statistics.put("monthFinish", 5);
            statistics.put("monthAdd", 3);
            
            // 产品列表
            List<Map<String, Object>> productList = new ArrayList<>();
            productList.add(Map.of("id", 1, "name", "实践教学管理平台"));
            productList.add(Map.of("id", 2, "name", "电子班牌管理系统"));
            productList.add(Map.of("id", 3, "name", "智慧校园(中学版)"));
            productList.add(Map.of("id", 4, "name", "在线考试批改"));
            productList.add(Map.of("id", 5, "name", "教务考试系统"));
            statistics.put("productList", productList);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未关闭产品统计数据失败: " + e.getMessage());
        }
    }
}
