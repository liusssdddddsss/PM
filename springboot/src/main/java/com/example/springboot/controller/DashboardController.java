package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.Requirement;
import com.example.springboot.entity.Task;
import com.example.springboot.entity.TestSuite;
import com.example.springboot.repository.ProductRepository;
import com.example.springboot.service.BugService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.RequirementService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.TestSuiteService;
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
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(value = "/dashboard", produces = "application/json;charset=UTF-8")
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
    @Resource
    ProjectService projectService;
    @Resource
    TestSuiteService testSuiteService;
    @Resource
    ProductRepository productRepository;

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

            // 昨日处理任务和Bug数（不再使用模拟数据）
            int yesterdayHandled = 0;
            Calendar yesterdayCal = Calendar.getInstance();
            yesterdayCal.add(Calendar.DAY_OF_YEAR, -1);

            List<Task> tasks = taskService.findall();
            for (Task task : tasks) {
                if (task.getStatus() != null && task.getStatus() == 2 && task.getCreatedAt() != null) {
                    Date d = tryParseDate(task.getCreatedAt());
                    if (d != null) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(d);
                        if (isSameDay(c, yesterdayCal)) {
                            yesterdayHandled++;
                        }
                    }
                }
            }

            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                if (bug.getStatus() != null && bug.getStatus() == 2) {
                    String timeStr = bug.getResolved_at() != null ? bug.getResolved_at() : bug.getCreated_at();
                    if (timeStr != null) {
                        Date d = tryParseDate(timeStr);
                        if (d != null) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            if (isSameDay(c, yesterdayCal)) {
                                yesterdayHandled++;
                            }
                        }
                    }
                }
            }

            // 不再返回模拟数据
            statistics.put("approveState", (int) approveCount);  // 待审批数
            statistics.put("taskState", (int) taskCount);      // 任务数
            statistics.put("bugState", (int) bugCount);        // BUG数
            statistics.put("needsState", (int) researchNeedsCount);      // 研发需求数
            statistics.put("userState", (int) userNeedsCount);       // 用户需求数
            // 当前无文档表，返回 0（避免模拟数据）
            statistics.put("passageState", 0);    // 文档数
            statistics.put("bug", yesterdayHandled);             // 昨日处理任务和Bug数
            
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
            
            // 从数据库获取真实数据
            Map<String, Integer> yesterday = new HashMap<>();
            Map<String, Integer> today = new HashMap<>();
            
            // 计算昨日完成的任务数（假设昨天是前一天）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            
            // 计算今日完成的任务数
            Calendar todayCalendar = Calendar.getInstance();
            
            // 从数据库获取任务数据
            List<Task> tasks = taskService.findall();
            int yesterdayTaskCount = 0;
            int todayTaskCount = 0;
            
            // 从数据库获取需求数据
            List<Requirement> requirements = requirementService.findall();
            int yesterdayCreateCount = 0;
            int todayCreateCount = 0;
            
            // 从数据库获取Bug数据
            List<Bug> bugs = bugService.findall();
            int yesterdayTiChuCount = 0;
            int todayTiChuCount = 0;
            int yesterdayBugCount = 0;
            int todayBugCount = 0;
            
            // 统计任务完成情况
            for (Task task : tasks) {
                if (task.getStatus() != null && task.getStatus() == 2) { // 已完成
                    if (task.getCreatedAt() != null) {
                        Calendar taskCalendar = Calendar.getInstance();
                        try {
                            // 直接处理String类型的日期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(task.getCreatedAt());
                            taskCalendar.setTime(date);
                            
                            if (isSameDay(taskCalendar, calendar)) {
                                yesterdayTaskCount++;
                            } else if (isSameDay(taskCalendar, todayCalendar)) {
                                todayTaskCount++;
                            }
                        } catch (Exception e) {
                            // 日期解析失败，跳过这条记录
                            System.out.println("日期解析失败: " + e.getMessage());
                        }
                    }
                }
            }
            
            // 统计需求创建情况
            for (Requirement requirement : requirements) {
                if (requirement.getCreated_at() != null) {
                    Calendar reqCalendar = Calendar.getInstance();
                    try {
                        // 直接处理String类型的日期
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(requirement.getCreated_at());
                        reqCalendar.setTime(date);
                        
                        if (isSameDay(reqCalendar, calendar)) {
                            yesterdayCreateCount++;
                        } else if (isSameDay(reqCalendar, todayCalendar)) {
                            todayCreateCount++;
                        }
                    } catch (Exception e) {
                        // 日期解析失败，跳过这条记录
                        System.out.println("日期解析失败: " + e.getMessage());
                    }
                }
            }
            
            // 统计Bug情况
            for (Bug bug : bugs) {
                if (bug.getCreated_at() != null) {
                    Calendar bugCalendar = Calendar.getInstance();
                    try {
                        // 直接处理String类型的日期
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(bug.getCreated_at());
                        bugCalendar.setTime(date);
                        
                        if (isSameDay(bugCalendar, calendar)) {
                            yesterdayTiChuCount++;
                        } else if (isSameDay(bugCalendar, todayCalendar)) {
                            todayTiChuCount++;
                        }
                    } catch (Exception e) {
                        // 日期解析失败，跳过这条记录
                        System.out.println("日期解析失败: " + e.getMessage());
                    }
                }
                
                if (bug.getStatus() != null && bug.getStatus() == 2) { // 已解决
                    if (bug.getCreated_at() != null) {
                        Calendar bugCalendar = Calendar.getInstance();
                        try {
                            // 直接处理String类型的日期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(bug.getCreated_at());
                            bugCalendar.setTime(date);
                            
                            if (isSameDay(bugCalendar, calendar)) {
                                yesterdayBugCount++;
                            } else if (isSameDay(bugCalendar, todayCalendar)) {
                                todayBugCount++;
                            }
                        } catch (Exception e) {
                            // 日期解析失败，跳过这条记录
                            System.out.println("日期解析失败: " + e.getMessage());
                        }
                    }
                }
            }
            
            // 计算工时消耗（不再使用模拟数据：基于 tasks.actualHours/estimatedHours 统计）
            int yesterdayClock = 0;
            int todayClock = 0;
            int yesterdayTaskForClock = 0;
            int todayTaskForClock = 0;

            for (Task task : tasks) {
                if (task.getStatus() == null || task.getStatus() != 2 || task.getCreatedAt() == null) {
                    continue;
                }

                Date d = tryParseDate(task.getCreatedAt());
                if (d == null) {
                    continue;
                }
                Calendar c = Calendar.getInstance();
                c.setTime(d);

                double hours = 0.0;
                if (task.getActualHours() != null) {
                    hours = task.getActualHours();
                } else if (task.getEstimatedHours() != null) {
                    hours = task.getEstimatedHours();
                }

                int rounded = (int) Math.round(hours);

                if (isSameDay(c, calendar)) {
                    yesterdayClock += rounded;
                    yesterdayTaskForClock++;
                } else if (isSameDay(c, todayCalendar)) {
                    todayClock += rounded;
                    todayTaskForClock++;
                }
            }

            int totalTasksForClock = yesterdayTaskForClock + todayTaskForClock;
            int averageClock = 0;
            if (totalTasksForClock > 0) {
                averageClock = (int) Math.round((yesterdayClock + todayClock) * 1.0 / totalTasksForClock);
            }
            
            yesterday.put("task", yesterdayTaskCount);
            yesterday.put("create", yesterdayCreateCount);
            yesterday.put("tiChu", yesterdayTiChuCount);
            yesterday.put("bug", yesterdayBugCount);
            yesterday.put("clock", yesterdayClock);
            yesterday.put("averageClock", averageClock);
            
            today.put("task", todayTaskCount);
            today.put("create", todayCreateCount);
            today.put("tiChu", todayTiChuCount);
            today.put("bug", todayBugCount);
            today.put("clock", todayClock);
            today.put("averageClock", averageClock);
            
            statistics.put("yesterday", yesterday);
            statistics.put("today", today);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取团队完成情况失败: " + e.getMessage());
        }
    }
    
    // 辅助方法：判断两个Calendar是否是同一天
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private Date tryParseDate(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(str);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Operation(summary = "获取产品总览数据", description = "返回产品总览数据")
    @GetMapping("/product-overview")
    public Result getProductOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 从数据库获取项目数据，计算产品相关统计
            Iterable<Project> projects = projectService.findAll();
            List<Project> projectList = new ArrayList<>();
            projects.forEach(projectList::add);
            
            // 计算产品总数（基于唯一的product_id）
            Set<Long> productIds = new HashSet<>();
            for (Project project : projectList) {
                if (project.getProduct_id() != null) {
                    productIds.add(project.getProduct_id());
                }
            }
            int productLineCount = productIds.size();
            int productCount = projectList.size();
            
            // 计算未完成计划数（状态不为2的项目）
            int unfinishedPlanCount = 0;
            // 计算未关闭需求数
            int unclosedNeedCount = 0;
            // 计算激活Bug数
            int activeBugCount = 0;
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            
            // 统计需求
            List<Requirement> requirements = requirementService.findall();
            for (Requirement req : requirements) {
                if (req.getStatus() == null || req.getStatus() < 2) {
                    unclosedNeedCount++;
                }
            }
            
            // 统计Bug
            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                if (bug.getStatus() == null || bug.getStatus() < 2) {
                    activeBugCount++;
                }
            }
            
            // 统计未完成计划
            for (Project project : projectList) {
                if (project.getStatus() == null || project.getStatus() != 2) {
                    unfinishedPlanCount++;
                }
            }
            
            overview.put("productLineCount", productLineCount);
            overview.put("productCount", productCount);
            overview.put("unfinishedPlanCount", unfinishedPlanCount);
            overview.put("unclosedNeedCount", unclosedNeedCount);
            overview.put("activeBugCount", activeBugCount);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品总览失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取产品年度推进统计数据", description = "返回产品年度推进统计数据")
    @GetMapping("/product-progress")
    public Result getProductProgress(String year) {
        try {
            Map<String, Object> progress = new HashMap<>();
            
            int selectedYear = year != null ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
            
            // 统计已完成发布数（项目状态为2且在指定年份完成）
            int completedReleaseCount = 0;
            int completedNeedCount = 0;
            int completedBugCount = 0;
            
            Iterable<Project> projectIterable = projectService.findAll();
            List<Project> projects = new ArrayList<>();
            projectIterable.forEach(projects::add);
            
            List<Requirement> requirements = requirementService.findall();
            List<Bug> bugs = bugService.findall();
            
            Calendar calendar = Calendar.getInstance();
            
            // 统计已完成的发布
            for (Project project : projects) {
                if (project.getStatus() != null && project.getStatus() == 2 && project.getEnd_date() != null) {
                    calendar.setTime(project.getEnd_date());
                    if (calendar.get(Calendar.YEAR) == selectedYear) {
                        completedReleaseCount++;
                    }
                }
            }
            
            // 统计已完成的需求
            for (Requirement req : requirements) {
                if (req.getStatus() != null && req.getStatus() == 2 && req.getCreated_at() != null) {
                    Date d = tryParseDate(req.getCreated_at());
                    if (d != null) {
                        calendar.setTime(d);
                        if (calendar.get(Calendar.YEAR) == selectedYear) {
                            completedNeedCount++;
                        }
                    }
                }
            }
            
            // 统计已完成的Bug
            for (Bug bug : bugs) {
                if (bug.getStatus() != null && bug.getStatus() == 2 && bug.getCreated_at() != null) {
                    Date d = tryParseDate(bug.getCreated_at());
                    if (d != null) {
                        calendar.setTime(d);
                        if (calendar.get(Calendar.YEAR) == selectedYear) {
                            completedBugCount++;
                        }
                    }
                }
            }
            
            progress.put("completedReleaseCount", completedReleaseCount);
            progress.put("completedNeedCount", completedNeedCount);
            progress.put("completedBugCount", completedBugCount);
            
            return Result.success(progress);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品年度推进统计失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取研发需求数据", description = "返回研发需求数据")
    @GetMapping("/requirements")
    public Result getRequirements(String username) {
        try {
            // 从数据库获取研发需求数据
            List<Requirement> requirements = requirementService.findall();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Requirement req : requirements) {
                Map<String, Object> reqMap = new HashMap<>();
                reqMap.put("id", req.getId());
                reqMap.put("name", req.getTitle());
                // 简化优先级映射
                String priority = "一般";
                if (req.getPriority() != null) {
                    switch (req.getPriority()) {
                        case 1: priority = "紧急"; break;
                        case 2: priority = "严重"; break;
                        case 3: priority = "正常"; break;
                        default: priority = "一般";
                    }
                }
                reqMap.put("priority", priority);
                result.add(reqMap);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取研发需求数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取未关闭的产品列表", description = "返回未关闭的产品列表")
    @GetMapping("/unclosed-products")
    public Result getUnclosedProducts() {
        try {
            // 从数据库获取未关闭的产品列表
            Iterable<Project> projectIterable = projectService.findAll();
            List<Project> projects = new ArrayList<>();
            projectIterable.forEach(projects::add);
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Project project : projects) {
                if (project.getStatus() == null || project.getStatus() != 2) {
                    Map<String, Object> projMap = new HashMap<>();
                    projMap.put("projectName", project.getName());
                    projMap.put("manager", "未指定");
                    // 模拟激活需求和激活Bug数
                    projMap.put("activeNeeds", (int)(Math.random() * 100));
                    projMap.put("completionRate", (int)(Math.random() * 100));
                    projMap.put("plan", (int)(Math.random() * 100));
                    projMap.put("activeBugs", (int)(Math.random() * 100));
                    projMap.put("release", (int)(Math.random() * 100));
                    result.add(projMap);
                }
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未关闭的产品列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取产品发布列表", description = "返回产品发布列表")
    @GetMapping("/product-releases")
    public Result getProductReleases() {
        try {
            // 从数据库获取产品发布列表
            Iterable<Project> projectIterable = projectService.findAll();
            List<Project> projects = new ArrayList<>();
            projectIterable.forEach(projects::add);
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Project project : projects) {
                Map<String, Object> releaseMap = new HashMap<>();
                releaseMap.put("projectName", project.getName());
                releaseMap.put("product", project.getName() != null ? project.getName() : "未指定");
                releaseMap.put("releaseDate", project.getEnd_date() != null ? project.getEnd_date().toString() : "未指定");
                String status = "未发布";
                if (project.getStatus() != null) {
                    switch (project.getStatus()) {
                        case 0: status = "未开始"; break;
                        case 1: status = "进行中"; break;
                        case 2: status = "已发布"; break;
                        default: status = "未知";
                    }
                }
                releaseMap.put("status", status);
                result.add(releaseMap);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品发布列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取产品年度工作量统计数据", description = "返回产品年度工作量统计数据")
    @GetMapping("/year-work-statistics")
    public Result getYearWorkStatistics(String year) {
        try {
            int selectedYear = year != null ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
            
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取产品列表
            Iterable<Product> products = productRepository.findAll();
            List<Map<String, Object>> demandSizeList = new ArrayList<>();
            List<Map<String, Object>> demandCountList = new ArrayList<>();
            List<Map<String, Object>> repairBugList = new ArrayList<>();
            
            for (Product product : products) {
                Map<String, Object> item1 = new HashMap<>();
                item1.put("name", product.getName());
                item1.put("count", (int)(Math.random() * 200));
                demandSizeList.add(item1);
                
                Map<String, Object> item2 = new HashMap<>();
                item2.put("name", product.getName());
                item2.put("count", (int)(Math.random() * 200));
                demandCountList.add(item2);
                
                Map<String, Object> item3 = new HashMap<>();
                item3.put("name", product.getName());
                item3.put("count", (int)(Math.random() * 200));
                repairBugList.add(item3);
            }
            
            statistics.put("demandSizeList", demandSizeList);
            statistics.put("demandCountList", demandCountList);
            statistics.put("repairBugList", repairBugList);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品年度工作量统计失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取月度发布数据", description = "返回月度发布数据")
    @GetMapping("/monthly-release")
    public Result getMonthlyRelease(String year) {
        try {
            int selectedYear = year != null ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
            
            Map<String, Object> result = new HashMap<>();
            
            // 生成月份数据
            String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
            int[] releaseCounts = new int[12];
            
            // 随机生成一些数据
            for (int i = 0; i < 12; i++) {
                releaseCounts[i] = (int)(Math.random() * 30);
            }
            
            result.put("months", months);
            result.put("releaseCounts", releaseCounts);
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取月度发布数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取年度发布榜数据", description = "返回年度发布榜数据")
    @GetMapping("/yearly-ranking")
    public Result getYearlyRanking(String year) {
        try {
            int selectedYear = year != null ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
            
            Map<String, Object> result = new HashMap<>();
            
            // 获取产品列表
            Iterable<Product> products = productRepository.findAll();
            List<String> productNames = new ArrayList<>();
            List<Integer> releaseCounts = new ArrayList<>();
            
            for (Product product : products) {
                productNames.add(product.getName());
                releaseCounts.add((int)(Math.random() * 200));
            }
            
            result.put("products", productNames);
            result.put("releaseCounts", releaseCounts);
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取年度发布榜数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目总览数据", description = "返回项目总览数据")
    @GetMapping("/project-overview")
    public Result getProjectOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 从数据库获取项目数据
            Iterable<Project> projects = projectService.findAll();
            List<Project> projectList = new ArrayList<>();
            projects.forEach(projectList::add);
            
            // 计算项目总数
            int projectCount = projectList.size();
            overview.put("xiangMuCount", projectCount);
            
            // 计算今年完成的项目数（status = 2 表示已完成）
            int thisYearFinish = 0;
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            
            // 统计近三年的项目数量
            int[] yearCounts = new int[3]; // 2022, 2023, 2024
            
            for (Project project : projectList) {
                // 检查是否是今年完成的项目
                if (project.getStatus() != null && project.getStatus() == 2) {
                    if (project.getEnd_date() != null) {
                        calendar.setTime(project.getEnd_date());
                        if (calendar.get(Calendar.YEAR) == currentYear) {
                            thisYearFinish++;
                        }
                    }
                }
                
                // 统计年份分布
                if (project.getCreated_at() != null) {
                    calendar.setTime(project.getCreated_at());
                    int projectYear = calendar.get(Calendar.YEAR);
                    if (projectYear == 2022) {
                        yearCounts[0]++;
                    } else if (projectYear == 2023) {
                        yearCounts[1]++;
                    } else if (projectYear == 2024) {
                        yearCounts[2]++;
                    }
                }
            }
            
            overview.put("thisYearFinish", thisYearFinish);
            
            // 项目年份分布数据
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
            seriesItem.put("data", yearCounts);
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
            
            // 从数据库获取任务数据
            List<Task> tasks = taskService.findall();
            
            // 计算任务总数
            int taskAllCount = tasks.size();
            overview.put("taskAllCount", taskAllCount);
            
            // 计算完成的任务数（status = 2 表示已完成）
            int taskFinishCount = 0;
            // 统计任务状态分布
            int[] statusCounts = new int[3]; // 未开始(0), 进行中(1), 已完成(2)
            
            for (Task task : tasks) {
                if (task.getStatus() != null) {
                    if (task.getStatus() == 2) {
                        taskFinishCount++;
                    }
                    if (task.getStatus() >= 0 && task.getStatus() < 3) {
                        statusCounts[task.getStatus()]++;
                    }
                }
            }
            
            overview.put("taskFinishCount", taskFinishCount);
            
            // 任务分布数据
            Map<String, Object> taskDistributionData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", new String[] {"未开始", "进行中", "已完成"});
            taskDistributionData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            taskDistributionData.put("yAxis", yAxis);
            
            Map<String, Object> seriesItem = new HashMap<>();
            seriesItem.put("type", "bar");
            seriesItem.put("data", statusCounts);
            taskDistributionData.put("series", new Map[] {seriesItem});
            
            overview.put("taskDistributionData", taskDistributionData);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取任务完成总览失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取单个项目统计详情", description = "用于工作台右侧“项目统计”卡片，返回一个代表性项目的工时、需求、任务、Bug 等统计")
    @GetMapping("/project-detail")
    public Result getProjectDetail() {
        try {
            // 选择一个代表性项目：优先选择未关闭/未归档的；如果没有就取任意一个
            Iterable<Project> allProjects = projectService.findAll();
            Project targetProject = null;

            for (Project p : allProjects) {
                if (p.getStatus() == null || (p.getStatus() != 2 && p.getStatus() != 3)) {
                    targetProject = p;
                    break;
                }
            }

            if (targetProject == null) {
                for (Project p : projectService.findAll()) {
                    targetProject = p;
                    break;
                }
            }

            if (targetProject == null) {
                // 没有任何项目时，直接返回空统计
                Map<String, Object> empty = new HashMap<>();
                empty.put("projectName", "暂无项目");
                empty.put("finishTime", null);
                empty.put("workTimeTotal", 0);
                empty.put("workTimeConsumed", 0);
                empty.put("workTimeRemaining", 0);
                empty.put("needTotal", 0);
                empty.put("needFinished", 0);
                empty.put("needUnclosed", 0);
                empty.put("taskTotal", 0);
                empty.put("taskNotStarted", 0);
                empty.put("taskInProgress", 0);
                empty.put("bugTotal", 0);
                empty.put("bugClosed", 0);
                empty.put("bugUnclosed", 0);
                return Result.success(empty);
            }

            Integer projectId = targetProject.getId().intValue();

            // 统计工时（从 tasks 表中获取）
            double totalHours = 0.0;
            double consumedHours = 0.0;
            int taskTotal = 0;
            int taskNotStarted = 0;
            int taskInProgress = 0;

            List<Task> tasks = taskService.findall();
            for (Task task : tasks) {
                if (task.getProjectId() != null && task.getProjectId().equals(projectId)) {
                    taskTotal++;

                    if (task.getEstimatedHours() != null) {
                        totalHours += task.getEstimatedHours();
                    }
                    if (task.getActualHours() != null) {
                        consumedHours += task.getActualHours();
                    }

                    Integer status = task.getStatus();
                    if (status == null || status == 0) {
                        taskNotStarted++;
                    } else if (status == 1) {
                        taskInProgress++;
                    }
                }
            }

            int workTimeTotal = (int) Math.round(totalHours);
            int workTimeConsumed = (int) Math.round(consumedHours);
            int workTimeRemaining = Math.max(0, workTimeTotal - workTimeConsumed);

            // 统计需求（从 requirements 表中获取）
            int needTotal = 0;
            int needFinished = 0;
            int needUnclosed = 0;
            List<Requirement> requirements = requirementService.findall();
            for (Requirement req : requirements) {
                if (req.getProject_id() != null && req.getProject_id().equals(projectId)) {
                    needTotal++;
                    Integer status = req.getStatus();
                    if (status != null && status == 2) {
                        needFinished++;
                    }
                    if (status == null || status < 2) {
                        needUnclosed++;
                    }
                }
            }

            // 统计 Bug（从 bugs 表中获取）
            int bugTotal = 0;
            int bugClosed = 0;
            int bugUnclosed = 0;
            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                if (bug.getProject_id() != null && bug.getProject_id().equals(projectId)) {
                    bugTotal++;
                    Integer status = bug.getStatus();
                    if (status != null && status == 2) {
                        bugClosed++;
                    }
                    if (status == null || status < 2) {
                        bugUnclosed++;
                    }
                }
            }

            Map<String, Object> detail = new HashMap<>();
            detail.put("projectName", targetProject.getName());
            detail.put("finishTime", targetProject.getEnd_date());
            detail.put("workTimeTotal", workTimeTotal);
            detail.put("workTimeConsumed", workTimeConsumed);
            detail.put("workTimeRemaining", workTimeRemaining);
            detail.put("needTotal", needTotal);
            detail.put("needFinished", needFinished);
            detail.put("needUnclosed", needUnclosed);
            detail.put("taskTotal", taskTotal);
            detail.put("taskNotStarted", taskNotStarted);
            detail.put("taskInProgress", taskInProgress);
            detail.put("bugTotal", bugTotal);
            detail.put("bugClosed", bugClosed);
            detail.put("bugUnclosed", bugUnclosed);

            return Result.success(detail);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目统计详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取未关闭产品统计数据", description = "用于工作台“未关闭的产品统计”卡片，基于需求和Bug计算整体交付情况")
    @GetMapping("/product-statistics")
    public Result getProductStatistics() {
        try {
            Map<String, Object> result = new HashMap<>();

            // 需求相关统计
            int validNeeds = 0;
            int deliveredNeeds = 0;
            int unclosedNeeds = 0;
            int monthFinish = 0;
            int monthAdd = 0;

            List<Requirement> requirements = requirementService.findall();
            Calendar now = Calendar.getInstance();
            int currentYear = now.get(Calendar.YEAR);
            int currentMonth = now.get(Calendar.MONTH);

            for (Requirement req : requirements) {
                Integer status = req.getStatus();
                if (status != null) {
                    validNeeds++;
                    if (status == 2) {
                        deliveredNeeds++;
                    }
                    if (status < 2) {
                        unclosedNeeds++;
                    }
                } else {
                    // 没有状态的也算有效且未关闭
                    validNeeds++;
                    unclosedNeeds++;
                }

                if (req.getCreated_at() != null) {
                    Date d = tryParseDate(req.getCreated_at());
                    if (d != null) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(d);
                        if (c.get(Calendar.YEAR) == currentYear && c.get(Calendar.MONTH) == currentMonth) {
                            monthAdd++;
                            if (status != null && status == 2) {
                                monthFinish++;
                            }
                        }
                    }
                }
            }

            int deliveryRate = 0;
            if (validNeeds > 0) {
                deliveryRate = (deliveredNeeds * 100) / validNeeds;
            }

            // 获取产品列表
            List<Map<String, Object>> productList = new ArrayList<>();
            // 从产品表中获取所有产品
            Iterable<Product> products = productRepository.findAll();
            for (Product product : products) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("name", product.getName());
                productList.add(productMap);
            }

            result.put("deliveryRate", deliveryRate);
            result.put("validNeeds", validNeeds);
            result.put("deliveredNeeds", deliveredNeeds);
            result.put("unclosedNeeds", unclosedNeeds);
            result.put("monthFinish", monthFinish);
            result.put("monthAdd", monthAdd);
            result.put("productList", productList);

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未关闭产品统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取需求统计数据", description = "返回需求统计数据")
    @GetMapping("/needs-statistics")
    public Result getNeedsStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 从数据库获取真实的需求统计数据
            Map<String, Object> needsChartData = new HashMap<>();
            Map<String, Object> xAxis = new HashMap<>();
            xAxis.put("type", "category");
            xAxis.put("data", new String[] {"7月", "8月", "9月", "10月", "11月", "12月"});
            needsChartData.put("xAxis", xAxis);
            
            Map<String, Object> yAxis = new HashMap<>();
            yAxis.put("type", "value");
            needsChartData.put("yAxis", yAxis);
            
            // 从数据库获取需求数据
            List<Requirement> requirements = requirementService.findall();
            
            // 统计每个月的需求数量
            int[] addData = new int[6]; // 7月到12月
            int[] finishData = new int[6]; // 7月到12月
            
            for (Requirement requirement : requirements) {
                if (requirement.getCreated_at() != null) {
                    Calendar calendar = Calendar.getInstance();
                    try {
                        // 直接处理String类型的日期
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(requirement.getCreated_at());
                        calendar.setTime(date);
                        
                        int month = calendar.get(Calendar.MONTH); // 0-11
                        
                        // 只统计7月到12月的数据
                        if (month >= 6 && month <= 11) {
                            int index = month - 6; // 0-5
                            addData[index]++;
                            
                            // 如果需求已完成
                            if (requirement.getStatus() != null && requirement.getStatus() == 2) {
                                finishData[index]++;
                            }
                        }
                    } catch (Exception e) {
                        // 日期解析失败，跳过这条记录
                        System.out.println("日期解析失败: " + e.getMessage());
                    }
                }
            }
            
            Map<String, Object> seriesItem1 = new HashMap<>();
            seriesItem1.put("name", "finish");
            seriesItem1.put("type", "line");
            seriesItem1.put("stack", "Total");
            seriesItem1.put("data", finishData);
            
            Map<String, Object> seriesItem2 = new HashMap<>();
            seriesItem2.put("name", "add");
            seriesItem2.put("type", "line");
            seriesItem2.put("stack", "Total");
            seriesItem2.put("data", addData);
            
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
            
            // 从数据库获取真实的测试统计数据
            
            // 计算昨日和今日的Bug数量
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            
            Calendar todayCalendar = Calendar.getInstance();
            
            // 从数据库获取Bug数据
            List<Bug> bugs = bugService.findall();
            int yesterdayNew = 0;
            int todayNew = 0;
            int yesterdaySolved = 0;
            int todaySolved = 0;
            int validBugs = 0;
            int fixedBugs = 0;
            int unclosedBugs = 0;
            
            for (Bug bug : bugs) {
                if (bug.getCreated_at() != null) {
                    Calendar bugCalendar = Calendar.getInstance();
                    try {
                        // 直接处理String类型的日期
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(bug.getCreated_at());
                        bugCalendar.setTime(date);
                        
                        if (isSameDay(bugCalendar, calendar)) {
                            yesterdayNew++;
                        } else if (isSameDay(bugCalendar, todayCalendar)) {
                            todayNew++;
                        }
                    } catch (Exception e) {
                        // 日期解析失败，跳过这条记录
                        System.out.println("日期解析失败: " + e.getMessage());
                    }
                }
                
                if (bug.getStatus() != null && bug.getStatus() == 2) { // 已解决
                    fixedBugs++;
                    if (bug.getCreated_at() != null) {
                        Calendar bugCalendar = Calendar.getInstance();
                        try {
                            // 直接处理String类型的日期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(bug.getCreated_at());
                            bugCalendar.setTime(date);
                            
                            if (isSameDay(bugCalendar, calendar)) {
                                yesterdaySolved++;
                            } else if (isSameDay(bugCalendar, todayCalendar)) {
                                todaySolved++;
                            }
                        } catch (Exception e) {
                            // 日期解析失败，跳过这条记录
                            System.out.println("日期解析失败: " + e.getMessage());
                        }
                    }
                } else {
                    unclosedBugs++;
                }
                
                validBugs++;
            }
            
            // 计算Bug修复率
            int bugRepairRate = 0;
            if (validBugs > 0) {
                bugRepairRate = (fixedBugs * 100) / validBugs;
            }
            
            // 获取待测试的测试单列表（从测试套件表中获取）
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<String> testLists = new ArrayList<>();
            
            for (TestSuite testSuite : testSuites) {
                testLists.add(testSuite.getName());
            }
            
            statistics.put("yesterdayNew", yesterdayNew);
            statistics.put("todayNew", todayNew);
            statistics.put("yesterdaySolved", yesterdaySolved);
            statistics.put("todaySolved", todaySolved);
            statistics.put("bugRepairRate", bugRepairRate);
            statistics.put("validBugs", validBugs);
            statistics.put("fixedBugs", fixedBugs);
            statistics.put("unclosedBugs", unclosedBugs);
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
    
    @Operation(summary = "获取测试用例列表", description = "返回测试用例列表")
    @GetMapping("/test-cases")
    public Result getTestCases() {
        try {
            // 从数据库获取测试用例数据
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (TestSuite testSuite : testSuites) {
                Map<String, Object> testCaseMap = new HashMap<>();
                testCaseMap.put("id", testSuite.getId());
                testCaseMap.put("name", testSuite.getName());
                testCaseMap.put("project_name", testSuite.getName()); // 假设项目名称就是测试套件名称
                testCaseMap.put("priority", 2); // 默认为一般优先级
                testCaseMap.put("status", 1); // 默认为待测试状态
                testCaseMap.put("due_date", new Date().toString()); // 默认为当前日期
                testCaseMap.put("progress", 0); // 默认为0%进度
                result.add(testCaseMap);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测试用例列表失败: " + e.getMessage());
        }
    }
}