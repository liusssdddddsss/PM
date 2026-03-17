package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.Requirement;
import com.example.springboot.entity.Task;
import com.example.springboot.service.BugService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.RequirementService;
import com.example.springboot.service.ProjectService;
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
    @Resource
    ProjectService projectService;

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
                    if (task.getCreated_at() != null) {
                        Calendar taskCalendar = Calendar.getInstance();
                        try {
                            // 直接处理String类型的日期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(task.getCreated_at());
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
            
            // 计算工时消耗（模拟数据，实际应该从工时表中获取）
            int yesterdayClock = 65;
            int todayClock = 50;
            int averageClock = 6;
            
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

    @Operation(summary = "获取产品总览数据", description = "返回产品总览数据")
    @GetMapping("/product-overview")
    public Result getProductOverview() {
        try {
            Map<String, Integer> overview = new HashMap<>();
            
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
            int productCount = productIds.size();
            
            // 计算今年发布的产品数（基于项目的创建年份）
            int thisYearIssue = 0;
            int closeCount = 0;
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            
            for (Project project : projectList) {
                // 今年发布的产品
                if (project.getCreated_at() != null) {
                    calendar.setTime(project.getCreated_at());
                    if (calendar.get(Calendar.YEAR) == currentYear) {
                        thisYearIssue++;
                    }
                }
                // 已关闭的产品（基于项目状态）
                if (project.getStatus() != null && project.getStatus() == 2) {
                    closeCount++;
                }
            }
            
            overview.put("projectCount", productCount);
            overview.put("thisYearIssue", thisYearIssue);
            overview.put("closeCount", closeCount);
            
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
            
            // 获取待测试的测试单列表（从任务表中获取状态为0或1的任务）
            List<Task> tasks = taskService.findall();
            List<String> testLists = new ArrayList<>();
            
            for (Task task : tasks) {
                if (task.getStatus() != null && (task.getStatus() == 0 || task.getStatus() == 1)) {
                    testLists.add(task.getTitle());
                }
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
}