package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.repository.ProjectMemberRepository;
import com.example.springboot.service.*;
import com.example.springboot.utils.RolePermissionUtils;
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
    private ProjectMemberService projectMemberService;


    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Operation(summary = "获取测试统计数据", description = "返回测试相关的统计数据")
    @GetMapping("/test-statistics")
    public Result getTestStatistics(@RequestParam(required = false) String projectName, @RequestParam(required = false) String username) {
        try {
            // 从数据库获取测试套件数据
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<Bug> bugs = bugService.findall();
            
            // 添加日志输出
            System.out.println("获取到的测试套件数量: " + testSuites.size());
            System.out.println("当前用户名: " + username);

            // 计算统计数据
            int yesterdayNew = 0;
            int yesterdayResolved = 0;
            int todayNew = 0;
            int todayResolved = 0;
            int yesterdayClosed = 0;
            int todayClosed = 0;

            // 获取昨天和今天的日期
            Calendar cal = Calendar.getInstance();
            Date todayDate = cal.getTime();
            cal.add(Calendar.DATE, -1);
            Date yesterdayDate = cal.getTime();

            // 格式化日期为yyyy-MM-dd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String todayStr = sdf.format(todayDate);
            String yesterdayStr = sdf.format(yesterdayDate);

            // 统计Bug数据
            int validBugs = 0;
            int resolvedBugs = 0;
            int unresolvedBugs = 0;

            // 检查用户角色
            boolean isProductManager = false;
            boolean isDeveloper = false;
            boolean isTester = false;
            boolean isAdmin = false;
            Long currentUserId = null;
            if (username != null) {
                User user = userService.findByUsername(username);
                if (user != null) {
                    System.out.println("用户角色ID: " + user.getRole_id());
                    if (user.getId() != null) {
                        currentUserId = user.getId().longValue();
                    }
                    // 角色ID定义：1=超级管理员，2=产品经理，3=开发者，4=测试者
                    if (user.getRole_id() != null && user.getRole_id() == 1) {
                        isAdmin = true;
                    } else if (user.getRole_id() != null && user.getRole_id() == 2) {
                        isProductManager = true;
                    } else if (user.getRole_id() != null && user.getRole_id() == 3) {
                        isDeveloper = true;
                    } else if (user.getRole_id() != null && user.getRole_id() == 4) {
                        isTester = true;
                    }
                }
            }
            System.out.println("是否为产品经理: " + isProductManager);
            System.out.println("是否为开发者: " + isDeveloper);
            System.out.println("当前用户ID: " + currentUserId);

            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                try {
                    // 尝试将用户名转换为Long类型（如果用户名是数字格式）
                    Long userId = Long.parseLong(username);
                    List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                    for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                        if (member.getProjectId() != null) {
                            userProjectIds.add(member.getProjectId());
                        }
                    }
                } catch (NumberFormatException e) {
                    // 用户名不是数字格式，尝试通过用户名查找用户
                    User user = userService.findByUsername(username);
                    if (user != null && user.getId() != null) {
                        Long userId = user.getId().longValue();
                        List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                        for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                            if (member.getProjectId() != null) {
                                userProjectIds.add(member.getProjectId());
                            }
                        }
                    }
                }
            }

            // 如果指定了项目名称，过滤出该项目的Bug
            Long targetProjectId = null;
            if (projectName != null && !projectName.isEmpty()) {
                // 1. 首先尝试通过projectName查找项目
                Iterable<Project> projects = projectService.findAll();
                for (Project project : projects) {
                    if (projectName.equals(project.getName())) {
                        targetProjectId = project.getId();
                        break;
                    }
                }
                
                // 2. 如果找不到项目，尝试通过projectName查找测试套件，然后获取测试套件关联的项目ID
                if (targetProjectId == null) {
                    for (TestSuite testSuite : testSuites) {
                        if (projectName.equals(testSuite.getName()) && testSuite.getProject_id() != null) {
                            targetProjectId = testSuite.getProject_id();
                            break;
                        }
                    }
                }
            }

            for (Bug bug : bugs) {
                // 如果指定了项目，只统计该项目的Bug
                if (targetProjectId != null) {
                    if (bug.getProjectId() == null || !bug.getProjectId().equals(targetProjectId.intValue())) {
                        continue;
                    }
                } else if (username != null) {
                    // 如果未指定项目，只统计用户参与项目的Bug
                    if (bug.getProjectId() != null) {
                        if (!userProjectIds.contains(Long.valueOf(bug.getProjectId()))) {
                            continue;
                        }
                    }
                }
                
                // 统计Bug状态
                if (bug.getStatus() != null) {
                    if (bug.getStatus() == 0 || bug.getStatus() == 1) {
                        validBugs++;
                        unresolvedBugs++;
                    } else if (bug.getStatus() == 2) {
                        validBugs++;
                        resolvedBugs++;
                    }
                }
                
                // 统计昨天和今天的Bug数据
                if (bug.getCreatedAt() != null) {
                    try {
                        // 直接比较字符串，假设createdAt格式为yyyy-MM-dd
                        String bugDate = bug.getCreatedAt().substring(0, Math.min(10, bug.getCreatedAt().length()));
                        if (bugDate.equals(yesterdayStr)) {
                            yesterdayNew++;
                        } else if (bugDate.equals(todayStr)) {
                            todayNew++;
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }
                
                if (bug.getStatus() != null && bug.getStatus() == 2) { // 已解决
                    if (bug.getResolvedAt() != null) {
                        try {
                            // 直接比较字符串，假设resolvedAt格式为yyyy-MM-dd
                            String bugDate = bug.getResolvedAt().substring(0, Math.min(10, bug.getResolvedAt().length()));
                            if (bugDate.equals(yesterdayStr)) {
                                yesterdayResolved++;
                            } else if (bugDate.equals(todayStr)) {
                                todayResolved++;
                            }
                        } catch (Exception e) {
                            // 解析失败，跳过
                        }
                    }
                }
            }

            // 计算修复率
            double bugRepairRate = 0;
            if (validBugs > 0) {
                bugRepairRate = (double) resolvedBugs / validBugs * 100;
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
            statistics.put("bugRepairRate", Math.round(bugRepairRate));
            
            // 提取测试列表（只显示用户有权限查看的）
            List<String> testLists = new ArrayList<>();
            for (TestSuite testSuite : testSuites) {
                System.out.println("测试套件ID: " + testSuite.getId() + ", 名称: " + testSuite.getName() + ", 负责人ID: " + testSuite.getAssignee_id());
                // 检查用户是否有权限查看该测试套件
                if (testSuite.getName() != null) {
                    if (username == null) {
                        // 未登录用户不显示
                        continue;
                    } else if (isAdmin || isProductManager || isTester) {
                        // 超级管理员、产品经理、测试者可以查看所有
                        testLists.add(testSuite.getName());
                    } else if (!isDeveloper && testSuite.getProject_id() != null && userProjectIds.contains(testSuite.getProject_id())) {
                        // 其他用户只能查看自己参与项目的测试套件
                        testLists.add(testSuite.getName());
                    }
                }
            }
            System.out.println("最终测试列表数量: " + testLists.size());
            statistics.put("testLists", testLists);

            // 提取模块列表（近期模块审核）
            List<String> modules = new ArrayList<>();
            for (TestSuite testSuite : testSuites) {
                // 检查用户是否有权限查看该测试套件
                if (testSuite.getName() != null) {
                    if (username == null) {
                        // 未登录用户不显示
                        continue;
                    } else if (isAdmin || isProductManager || isTester) {
                        // 超级管理员、产品经理、测试者可以查看所有
                        modules.add(testSuite.getName());
                    } else if (!isDeveloper && testSuite.getProject_id() != null && userProjectIds.contains(testSuite.getProject_id())) {
                        // 其他用户只能查看自己参与项目的测试套件
                        modules.add(testSuite.getName());
                    }
                }
            }
            statistics.put("modules", modules);

            // 提取待测试的测试单（开发者不可以查看）
            List<Map<String, Object>> pendingTests = new ArrayList<>();
            if (!isDeveloper) { // 开发者不可以查看待测试的测试单
                for (TestSuite testSuite : testSuites) {
                    if (testSuite.getStatus() != null && testSuite.getStatus() == 1) { // 1表示待测试
                        // 检查用户是否有权限查看该测试套件
                        if (testSuite.getName() != null) {
                            if (isProductManager) {
                                // 产品经理可以查看所有
                                addPendingTest(testSuite, productService, pendingTests);
                            } else if (testSuite.getProject_id() != null && userProjectIds.contains(testSuite.getProject_id())) {
                                // 测试者只能查看自己参与项目的待测试测试单
                                addPendingTest(testSuite, productService, pendingTests);
                            }
                        }
                    }
                }
            }
            statistics.put("pendingTests", pendingTests);

            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测试统计数据失败: " + e.getMessage());
        }
    }
    
    // 辅助方法：添加待测试的测试单
    private void addPendingTest(TestSuite testSuite, ProductService productService, List<Map<String, Object>> pendingTests) {
        Map<String, Object> test = new HashMap<>();
        test.put("name", testSuite.getName());
        
        // 转换优先级
        String priority = "一般";
        if (testSuite.getPriority() != null) {
            switch (testSuite.getPriority()) {
                case 1: priority = "紧急"; break;
                case 2: priority = "一般"; break;
                case 3: priority = "正常"; break;
            }
        }
        test.put("priority", priority);
        
        // 获取产品名称
        String productName = "未知产品";
        if (testSuite.getProduct_id() != null) {
            try {
                Optional<Product> productOpt = productService.findById(testSuite.getProduct_id());
                if (productOpt.isPresent()) {
                    productName = productOpt.get().getName();
                }
            } catch (Exception e) {
                // 如果获取产品名称失败，使用默认值
            }
        }
        test.put("product", productName);
        
        // 开始日期和结束日期
        test.put("startDate", testSuite.getStart_date() != null ? testSuite.getStart_date() : "");
        test.put("endDate", testSuite.getEnd_date() != null ? testSuite.getEnd_date() : "");
        
        pendingTests.add(test);
    }

    @Operation(summary = "获取用户测试任务", description = "返回当前用户的测试任务列表")
    @GetMapping("/user-test-tasks")
    public Result getUserTestTasks(@RequestParam String username) {
        try {
            // 检查用户角色
            User user = userService.findByUsername(username);
            if (user != null && user.getRole_id() != null && user.getRole_id() == 3) {
                // 开发者无权限查看测试数据
                return Result.success(new ArrayList<>());
            }
            
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
            // 检查用户角色
            User user = userService.findByUsername(username);
            if (user != null && user.getRole_id() != null && user.getRole_id() == 3) {
                // 开发者无权限查看测试数据
                return Result.success(new ArrayList<>());
            }
            
            // 从数据库获取用户的Bug列表
            List<Bug> bugs = bugService.findall();
            List<Map<String, Object>> userBugs = new ArrayList<>();
            
            for (Bug bug : bugs) {
                // 只返回指派给当前用户的Bug
                // 注意：assigneeId 存储的是用户ID（Integer），需要转换为字符串与username比较
                if (bug.getAssigneeId() != null) {
                    try {
                        // 尝试将assigneeId转换为字符串与username比较
                        if (bug.getAssigneeId().toString().equals(username)) {
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
                            if (bug.getProjectId() != null) {
                                try {
                                    Optional<Project> projectOpt = projectService.findById(bug.getProjectId().longValue());
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
                    } catch (Exception e) {
                        // 忽略类型转换错误，继续处理下一个Bug
                    }
                }
            }
            
            // 如果没有Bug数据，返回模拟数据
            if (userBugs.isEmpty()) {
                // 添加一些模拟Bug数据
                userBugs.add(Map.of(
                    "id", 1,
                    "name", "登录页面样式问题",
                    "project", "智慧教室系统",
                    "priority", "一般",
                    "status", "待处理",
                    "deadline", "",
                    "progress", 0
                ));
                userBugs.add(Map.of(
                    "id", 2,
                    "name", "数据提交失败",
                    "project", "教务考试系统",
                    "priority", "紧急",
                    "status", "处理中",
                    "deadline", "",
                    "progress", 50
                ));
                userBugs.add(Map.of(
                    "id", 3,
                    "name", "响应速度慢",
                    "project", "电子班牌系统",
                    "priority", "正常",
                    "status", "已解决",
                    "deadline", "",
                    "progress", 100
                ));
            }
            
            return Result.success(userBugs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户Bug列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取最新动态数据", description = "返回最新动态数据")
    @GetMapping("/dynamic")
    public Result getDynamicData(@RequestParam(required = false) String username) {
        try {
            // 从数据库获取操作日志数据
            List<OperationLog> operationLogs = operationLogService.findall();
            
            // 按时间倒序排序，越靠近现在的时间越在前
            operationLogs.sort((o1, o2) -> {
                if (o1.getCreated_at() == null || o2.getCreated_at() == null) {
                    return 0;
                }
                return o2.getCreated_at().compareTo(o1.getCreated_at());
            });
            
            List<Map<String, String>> dynamicData = new ArrayList<>();
            
            // 从数据库获取所有用户
            Iterable<User> allUsers = userService.findAll();
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                try {
                    // 尝试将用户名转换为Long类型（如果用户名是数字格式）
                    Long userId = Long.parseLong(username);
                    List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                    for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                        if (member.getProjectId() != null) {
                            userProjectIds.add(member.getProjectId());
                        }
                    }
                } catch (NumberFormatException e) {
                    // 用户名不是数字格式，尝试通过用户名查找用户
                    User user = userService.findByUsername(username);
                    if (user != null && user.getId() != null) {
                        Long userId = user.getId().longValue();
                        List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                        for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                            if (member.getProjectId() != null) {
                                userProjectIds.add(member.getProjectId());
                            }
                        }
                    }
                }
            }
            
            // 转换操作日志为前端需要的格式
            for (OperationLog log : operationLogs) {
                // 检查是否是用户团队内的动态
                if (username != null) {
                    // 这里简化处理，假设操作日志中的module字段包含项目信息
                    // 实际项目中可能需要更复杂的逻辑来判断动态是否与用户团队相关
                    boolean isTeamRelated = false;
                    
                    // 1. 操作人是用户自己
                    if (log.getUser_id() != null && log.getUser_id().equals(username)) {
                        isTeamRelated = true;
                    }
                    
                    // 2. 操作与用户参与的项目相关
                    if (!isTeamRelated && log.getModule() != null) {
                        // 尝试从module字段中提取项目信息
                        // 这里简化处理，实际项目中可能需要更复杂的解析
                        String module = log.getModule();
                        for (Long projectId : userProjectIds) {
                            if (module.contains(projectId.toString())) {
                                isTeamRelated = true;
                                break;
                            }
                        }
                    }
                    
                    // 如果不是团队相关的动态，跳过
                    if (!isTeamRelated) {
                        continue;
                    }
                }
                
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
                            // 直接使用String类型比较，因为log.getUser_id()现在是String类型
                            if (user.getUsername() != null && user.getUsername().equals(log.getUser_id())) {
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
            // 从数据库获取测试套件数据
            List<TestSuite> testSuites = testSuiteService.findAll();
            List<Map<String, Object>> testCases = new ArrayList<>();
            
            for (TestSuite testSuite : testSuites) {
                Map<String, Object> testCase = new HashMap<>();
                testCase.put("id", testSuite.getId());
                testCase.put("project_name", testSuite.getProject_id() != null ? getProjectName(testSuite.getProject_id()) : "未知项目");
                testCase.put("name", testSuite.getName());
                testCase.put("priority", testSuite.getPriority());
                testCase.put("status", testSuite.getStatus());
                testCase.put("due_date", testSuite.getEnd_date());
                testCase.put("progress", 0); // TestSuite类没有getProgress()方法，使用默认值0
                testCase.put("created_at", testSuite.getCreated_at());
                testCase.put("assignee", testSuite.getAssignee_id() != null ? getUserName(testSuite.getAssignee_id()) : "");
                testCases.add(testCase);
            }
            
            return Result.success(testCases);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测试用例列表失败: " + e.getMessage());
        }
    }
    
    // 辅助方法：根据项目ID获取项目名称
    private String getProjectName(Long projectId) {
        try {
            Optional<Project> projectOpt = projectService.findById(projectId);
            if (projectOpt.isPresent()) {
                return projectOpt.get().getName();
            }
        } catch (Exception e) {
            // 忽略错误，返回默认值
        }
        return "未知项目";
    }
    
    // 辅助方法：根据用户ID获取用户名
    private String getUserName(Object userId) {
        try {
            if (userId != null) {
                String userIdStr = userId.toString();
                User user = userService.findByUsername(userIdStr);
                if (user != null) {
                    return user.getName() != null ? user.getName() : user.getUsername();
                }
            }
        } catch (Exception e) {
            // 忽略错误，返回空字符串
        }
        return "";
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

    @Autowired
    private IterationService iterationService;

    @Operation(summary = "获取产品总览数据", description = "返回产品总览数据")
    @GetMapping("/product-overview")
    public Result getProductOverview(@RequestParam(required = false) String username) {
        try {
            // 从数据库获取产品总览数据
            Map<String, Object> overview = new HashMap<>();
            
            // 产品总数
            int productCount = 0;
            Iterable<Product> products = productService.findAll();
            for (Product product : products) {
                // 如果指定了用户名，只统计与该用户有关的产品
                if (username != null) {
                    if (product.getOwner_id() != null) {
                        // 尝试通过owner_id查找用户
                        User user = userService.findByUsername(product.getOwner_id().toString());
                        // 或者如果产品的owner_id与当前用户名匹配，也应该统计
                        if (user != null && username.equals(user.getUsername()) || 
                            product.getOwner_id().toString().equals(username)) {
                            productCount++;
                        }
                    }
                } else {
                    productCount++;
                }
            }
            overview.put("productCount", productCount);
            
            // 产品线总量（暂时与产品总数相同）
            int productLineCount = productCount;
            overview.put("productLineCount", productLineCount);
            
            // 项目总数
            int projectCount = 0;
            List<Project> projects = (List<Project>) projectService.findAll();
            for (Project project : projects) {
                // 如果指定了用户名，只统计与该用户有关的项目
                if (username != null) {
                    // 使用managerId字段，假设managerId对应的用户就是负责人
                    if (project.getManagerId() != null) {
                        User user = userService.findByUsername(project.getManagerId().toString());
                        if (user != null && username.equals(user.getUsername())) {
                            projectCount++;
                        }
                    }
                } else {
                    projectCount++;
                }
            }
            overview.put("unclosedNeedCount", projectCount);
            
            // 激活Bug数
            int activeBugCount = 0;
            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                // 如果指定了用户名，只统计与该用户有关的Bug
                if (username != null) {
                    if (bug.getAssigneeId() != null && bug.getAssigneeId().toString().equals(username)) {
                        if (bug.getStatus() != null && bug.getStatus() != 2) { // 假设2表示已解决
                            activeBugCount++;
                        }
                    }
                } else {
                    if (bug.getStatus() != null && bug.getStatus() != 2) { // 假设2表示已解决
                        activeBugCount++;
                    }
                }
            }
            overview.put("activeBugCount", activeBugCount);
            
            // 迭代项目总数
            int iterationProjectCount = 0;
            try {
                List<Iteration> iterations = iterationService.findAll();
                for (Iteration iteration : iterations) {
                    // 如果指定了用户名，只统计与该用户有关的迭代
                    if (username != null) {
                        // 通过迭代的projectId找到对应的项目，再判断项目是否与当前用户有关
                            if (iteration.getProjectId() != null) {
                                for (Project project : projects) {
                                    if (project.getId().intValue() == iteration.getProjectId() && project.getManagerId() != null) {
                                        User user = userService.findByUsername(project.getManagerId().toString());
                                        if (user != null && username.equals(user.getUsername())) {
                                            iterationProjectCount++;
                                        }
                                        break;
                                    }
                                }
                            }
                    } else {
                        iterationProjectCount++;
                    }
                }
            } catch (Exception e) {
                // 如果迭代服务不可用，默认为0
                iterationProjectCount = 0;
            }
            overview.put("iterationProjectCount", iterationProjectCount);
            
            // 未完成计划数（未完成的项目数加未完成的迭代数总和）
            int unfinishedProjectCount = 0;
            for (Project project : projects) {
                // 如果指定了用户名，只统计与该用户有关的项目
                if (username != null) {
                    // 使用managerId字段
                    if (project.getManagerId() != null) {
                        User user = userService.findByUsername(project.getManagerId().toString());
                        if (user != null && username.equals(user.getUsername())) {
                            if (project.getStatus() != null && project.getStatus() != 2) { // 假设2表示已完成
                                unfinishedProjectCount++;
                            }
                        }
                    }
                } else {
                    if (project.getStatus() != null && project.getStatus() != 2) { // 假设2表示已完成
                        unfinishedProjectCount++;
                    }
                }
            }
            
            int unfinishedIterationCount = 0;
            try {
                List<Iteration> iterations = iterationService.findAll();
                for (Iteration iteration : iterations) {
                    // 如果指定了用户名，只统计与该用户有关的迭代
                    if (username != null) {
                        // 通过迭代的projectId找到对应的项目，再判断项目是否与当前用户有关
                        if (iteration.getProjectId() != null) {
                            for (Project project : projects) {
                                if (project.getId().intValue() == iteration.getProjectId() && project.getManagerId() != null) {
                                    User user = userService.findByUsername(project.getManagerId().toString());
                                    if (user != null && username.equals(user.getUsername())) {
                                        if (iteration.getStatus() != null && iteration.getStatus() != 2) { // 假设2表示已完成
                                            unfinishedIterationCount++;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        if (iteration.getStatus() != null && iteration.getStatus() != 2) { // 假设2表示已完成
                            unfinishedIterationCount++;
                        }
                    }
                }
            } catch (Exception e) {
                // 如果迭代服务不可用，默认为0
                unfinishedIterationCount = 0;
            }
            
            int unfinishedPlanCount = unfinishedProjectCount + unfinishedIterationCount;
            overview.put("unfinishedPlanCount", unfinishedPlanCount);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品总览数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目总览数据", description = "返回项目总览数据")
    @GetMapping("/project-overview")
    public Result getProjectOverview(@RequestParam(required = false) String username) {
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
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                User user = userService.findByUsername(username);
                if (user != null) {
                    try {
                        // 尝试将用户名转换为Long类型（如果用户名是数字格式）
                        Long userId = Long.parseLong(username);
                        List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                        for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                            if (member.getProjectId() != null) {
                                userProjectIds.add(member.getProjectId());
                            }
                        }
                    } catch (NumberFormatException e) {
                        // 用户名不是数字格式，返回空列表
                    }
                }
            }
            
            Iterable<Project> projects = projectService.findAll();
            for (Project project : projects) {
                // 如果指定了用户名，只统计用户参与的项目
                if (username != null) {
                    if (!userProjectIds.contains(project.getId())) {
                        continue;
                    }
                }
                
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
            // 从数据库获取产品年度推进统计数据
            Map<String, Object> progress = new HashMap<>();
            
            // 已完成发布数
            int completedReleaseCount = 0;
            // 已完成项目数
            int completedNeedCount = 0;
            // 已完成Bug数
            int completedBugCount = 0;
            
            // 获取指定年份
            int targetYear = Integer.parseInt(year);
            
            // 统计已完成的发布数和项目数
            Iterable<Project> projects = projectService.findAll();
            Calendar cal = Calendar.getInstance();
            for (Project project : projects) {
                if (project.getStatus() != null && project.getStatus() == 2) { // 假设2表示已完成
                    if (project.getEnd_date() != null) {
                        cal.setTime(project.getEnd_date());
                        if (cal.get(Calendar.YEAR) == targetYear) {
                            completedReleaseCount++;
                            completedNeedCount++;
                        }
                    }
                }
            }
            
            // 统计已完成的Bug数
            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                if (bug.getStatus() != null && bug.getStatus() == 2) { // 假设2表示已解决
                    if (bug.getResolvedAt() != null) {
                        // 尝试从字符串中提取年份
                        try {
                            // 假设resolvedAt格式为yyyy-MM-dd或yyyy-MM-dd HH:mm:ss
                            String resolvedAt = bug.getResolvedAt();
                            if (resolvedAt.length() >= 4) {
                                int bugYear = Integer.parseInt(resolvedAt.substring(0, 4));
                                if (bugYear == targetYear) {
                                    completedBugCount++;
                                }
                            }
                        } catch (Exception e) {
                            // 解析失败，跳过
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
    public Result getUnclosedProducts(@RequestParam(required = false) String username) {
        try {
            // 从数据库获取未关闭的产品列表数据
            List<Map<String, Object>> products = new ArrayList<>();
            
            // 获取所有项目
            List<Project> allProjects = (List<Project>) projectService.findAll();
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                try {
                    // 将用户名转换为Long类型
                    Long userId = Long.parseLong(username);
                    // 获取用户参与的项目成员记录
                    List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                    for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                        if (member.getProjectId() != null) {
                            userProjectIds.add(member.getProjectId());
                        }
                    }
                } catch (Exception e) {
                    // 如果获取项目成员失败，继续执行
                }
            }
            
            // 获取所有产品
            Iterable<Product> productList = productService.findAll();
            for (Product product : productList) {
                // 只返回未关闭的产品（status != 2）
                if (product.getStatus() == null || product.getStatus() != 2) {
                    // 如果指定了用户名，只返回与该用户有关的产品
                    if (username != null) {
                        boolean isUserRelated = false;
                        
                        // 检查产品的owner_id是否与用户相关
                        if (product.getOwner_id() != null) {
                            // 尝试通过owner_id查找用户
                            Optional<User> userOptional = userService.findById(product.getOwner_id().toString());
                            // 或者如果产品的owner_id与当前用户名匹配，也应该显示
                            if (userOptional.isPresent() && username.equals(userOptional.get().getUsername()) || 
                                product.getOwner_id().toString().equals(username)) {
                                isUserRelated = true;
                            }
                        }
                        
                        // 检查用户是否参与了该产品的任何项目
                        if (!isUserRelated) {
                            for (Project project : allProjects) {
                                if (project.getProduct_id() != null && project.getProduct_id().equals(product.getId()) && 
                                    userProjectIds.contains(project.getId())) {
                                    isUserRelated = true;
                                    break;
                                }
                            }
                        }
                        
                        // 如果产品与用户无关，跳过
                        if (!isUserRelated) {
                            continue;
                        }
                    }
                    
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("projectName", product.getName());
                    
                    // 获取负责人信息
                    if (product.getOwner_id() != null) {
                        Optional<User> userOptional = userService.findById(product.getOwner_id().toString());
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            productMap.put("manager", user.getName());
                        } else {
                            productMap.put("manager", "未知");
                        }
                    } else {
                        productMap.put("manager", "未知");
                    }
                    
                    // 计算该产品相关的项目数
                    int productProjectCount = 0;
                    for (Project project : allProjects) {
                        // 过滤出与当前产品相关的项目
                        if (project.getProduct_id() != null && project.getProduct_id().equals(product.getId())) {
                            // 如果指定了用户名，只统计与该用户有关的项目
                            if (username != null) {
                                // 检查项目是否由用户管理或用户是项目成员
                                boolean isProjectRelated = false;
                                if (project.getManagerId() != null) {
                                    Optional<User> userOptional = userService.findById(project.getManagerId().toString());
                                    if (userOptional.isPresent() && username.equals(userOptional.get().getUsername())) {
                                        isProjectRelated = true;
                                    }
                                }
                                if (!isProjectRelated && userProjectIds.contains(project.getId())) {
                                    isProjectRelated = true;
                                }
                                if (isProjectRelated) {
                                    productProjectCount++;
                                }
                            } else {
                                productProjectCount++;
                            }
                        }
                    }
                    productMap.put("activeNeeds", productProjectCount);
                    
                    // 计算该产品相关的迭代项目数
                    int productIterationCount = 0;
                    try {
                        List<Iteration> iterations = iterationService.findAll();
                        for (Iteration iteration : iterations) {
                            // 过滤出与当前产品相关的迭代
                            if (iteration.getProjectId() != null) {
                                // 通过项目ID找到对应的项目，再判断项目是否属于当前产品
                                for (Project project : allProjects) {
                                    if (project.getId().intValue() == iteration.getProjectId() && 
                                        project.getProduct_id() != null && 
                                        project.getProduct_id().equals(product.getId())) {
                                        // 如果指定了用户名，只统计与该用户有关的迭代
                                        if (username != null) {
                                            // 检查项目是否与用户相关
                                            boolean isProjectRelated = false;
                                            if (project.getManagerId() != null) {
                                                Optional<User> userOptional = userService.findById(project.getManagerId().toString());
                                                if (userOptional.isPresent() && username.equals(userOptional.get().getUsername())) {
                                                    isProjectRelated = true;
                                                }
                                            }
                                            if (!isProjectRelated && userProjectIds.contains(project.getId())) {
                                                isProjectRelated = true;
                                            }
                                            if (isProjectRelated) {
                                                productIterationCount++;
                                            }
                                        } else {
                                            productIterationCount++;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 如果迭代服务不可用，默认为0
                        productIterationCount = 0;
                    }
                    productMap.put("iterationCount", productIterationCount);
                    productMap.put("plan", "进行中");
                    
                    // 计算该产品相关的Bug数
                    int productBugCount = 0;
                    List<Bug> bugs = bugService.findall();
                    for (Bug bug : bugs) {
                        // 过滤出与当前产品相关的Bug
                    if (bug.getProjectId() != null) {
                        // 查找Bug所属的项目，再判断项目是否属于当前产品
                        for (Project project : allProjects) {
                            if (project.getId().intValue() == bug.getProjectId() && 
                                project.getProduct_id() != null && 
                                project.getProduct_id().equals(product.getId())) {
                                    // 如果指定了用户名，只统计与该用户有关的Bug
                                    if (username != null) {
                                        // 检查Bug是否指派给用户，或者项目是否与用户相关
                                        boolean isBugRelated = false;
                                        if (bug.getAssigneeId() != null && bug.getAssigneeId().toString().equals(username)) {
                                            isBugRelated = true;
                                        }
                                        if (!isBugRelated) {
                                            if (project.getManagerId() != null) {
                                                Optional<User> userOptional = userService.findById(project.getManagerId().toString());
                                                if (userOptional.isPresent() && username.equals(userOptional.get().getUsername())) {
                                                    isBugRelated = true;
                                                }
                                            }
                                            if (!isBugRelated && userProjectIds.contains(project.getId())) {
                                                isBugRelated = true;
                                            }
                                        }
                                        if (isBugRelated) {
                                            productBugCount++;
                                        }
                                    } else {
                                        productBugCount++;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    productMap.put("activeBugs", productBugCount);
                    productMap.put("release", "计划中");
                    
                    products.add(productMap);
                }
            }
            
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未关闭的产品列表数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取产品开发周期分布数据", description = "返回产品开发周期分布数据")
    @GetMapping("/product-development-cycle")
    public Result getProductDevelopmentCycle(@RequestParam(required = false) String username) {
        try {
            // 从数据库获取产品开发周期分布数据
            Map<String, Object> cycleData = new HashMap<>();
            
            // 获取所有项目
            List<Project> allProjects = (List<Project>) projectService.findAll();
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                try {
                    // 将用户名转换为Long类型
                    Long userId = Long.parseLong(username);
                    // 获取用户参与的项目成员记录
                    List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                    for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                        if (member.getProjectId() != null) {
                            userProjectIds.add(member.getProjectId());
                        }
                    }
                } catch (Exception e) {
                    // 如果获取项目成员失败，继续执行
                }
            }
            
            // 获取所有产品
            Iterable<Product> productList = productService.findAll();
            
            // 计算不同开发周期的产品数量
            int lessThan3Months = 0;
            int between3And6Months = 0;
            int between6And12Months = 0;
            int moreThan12Months = 0;
            
            // 按开发周期分组的产品列表
            Map<String, List<Map<String, Object>>> productsByCycle = new HashMap<>();
            productsByCycle.put("3个月以内", new ArrayList<>());
            productsByCycle.put("3-6个月", new ArrayList<>());
            productsByCycle.put("6-12个月", new ArrayList<>());
            productsByCycle.put("12个月以上", new ArrayList<>());
            
            // 计算当前日期
            Date now = new Date();
            
            for (Product product : productList) {
                // 检查产品是否与用户相关
                boolean isUserRelated = false;
                if (username != null) {
                    // 检查产品的owner_id是否与用户相关
                    if (product.getOwner_id() != null) {
                        // 尝试通过owner_id查找用户
                        User user = userService.findByUsername(product.getOwner_id().toString());
                        // 或者如果产品的owner_id与当前用户名匹配，也应该显示
                        if (user != null && username.equals(user.getUsername()) || 
                            product.getOwner_id().toString().equals(username)) {
                            isUserRelated = true;
                        }
                    }
                    
                    // 检查用户是否参与了该产品的任何项目
                    if (!isUserRelated) {
                        for (Project project : allProjects) {
                            if (project.getProduct_id() != null && project.getProduct_id().equals(product.getId()) && 
                                userProjectIds.contains(project.getId())) {
                                isUserRelated = true;
                                break;
                            }
                        }
                    }
                    
                    // 如果产品与用户无关，跳过
                    if (!isUserRelated) {
                        continue;
                    }
                }
                
                // 计算产品开发周期（从创建日期到现在）
                long months = 0;
                if (product.getCreated_at() != null) {
                    long diffInMillis = now.getTime() - product.getCreated_at().getTime();
                    months = diffInMillis / (1000L * 60 * 60 * 24 * 30);
                }
                
                // 构建产品信息
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("id", product.getId());
                productInfo.put("name", product.getName());
                
                // 根据开发周期分组
                if (months < 3) {
                    lessThan3Months++;
                    productsByCycle.get("3个月以内").add(productInfo);
                } else if (months >= 3 && months < 6) {
                    between3And6Months++;
                    productsByCycle.get("3-6个月").add(productInfo);
                } else if (months >= 6 && months < 12) {
                    between6And12Months++;
                    productsByCycle.get("6-12个月").add(productInfo);
                } else {
                    moreThan12Months++;
                    productsByCycle.get("12个月以上").add(productInfo);
                }
            }
            
            // 构建饼图数据
            List<Map<String, Object>> pieData = new ArrayList<>();
            pieData.add(Map.of("value", lessThan3Months, "name", "3个月以内"));
            pieData.add(Map.of("value", between3And6Months, "name", "3-6个月"));
            pieData.add(Map.of("value", between6And12Months, "name", "6-12个月"));
            pieData.add(Map.of("value", moreThan12Months, "name", "12个月以上"));
            
            // 构建返回数据
            cycleData.put("pieData", pieData);
            cycleData.put("productsByCycle", productsByCycle);
            
            return Result.success(cycleData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品开发周期分布数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品发布列表数据", description = "返回产品发布列表数据")
    @GetMapping("/product-releases")
    public Result getProductReleases(@RequestParam String username) {
        try {
            // 模拟产品发布列表数据，但根据当前登录用户的用户名来过滤
            List<Map<String, Object>> releases = new ArrayList<>();
            
            // 只有当用户名为202201时，返回产品发布列表数据
            if ("202201".equals(username)) {
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
            }
            
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
            // 从数据库获取产品年度工作量统计数据
            Map<String, Object> statistics = new HashMap<>();
            
            // 完成需求规模
            List<Map<String, Object>> demandSizeList = new ArrayList<>();
            int smallDemandCount = 0;
            int mediumDemandCount = 0;
            int largeDemandCount = 0;
            
            // 完成需求数（按季度）
            List<Map<String, Object>> demandCountList = new ArrayList<>();
            int[] quarterlyDemandCount = new int[4]; // 四个季度
            
            // 修复Bug数（按季度）
            List<Map<String, Object>> repairBugList = new ArrayList<>();
            int[] quarterlyBugCount = new int[4]; // 四个季度
            
            // 获取指定年份
            int targetYear = Integer.parseInt(year);
            

            
            // 统计Bug修复数据
            List<Bug> bugs = bugService.findall();
            Calendar cal = Calendar.getInstance();
            for (Bug bug : bugs) {
                if (bug.getStatus() != null && bug.getStatus() == 2) { // 假设2表示已解决
                    if (bug.getResolvedAt() != null) {
                        // 尝试解析resolvedAt字段
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(bug.getResolvedAt());
                            cal.setTime(date);
                            if (cal.get(Calendar.YEAR) == targetYear) {
                                // 按季度统计
                                int quarter = (cal.get(Calendar.MONTH) / 3) + 1;
                                if (quarter >= 1 && quarter <= 4) {
                                    quarterlyBugCount[quarter - 1]++;
                                }
                            }
                        } catch (Exception e) {
                            // 解析失败，跳过
                        }
                    }
                }
            }
            

            
            // 构建季度Bug修复列表
            repairBugList.add(Map.of("name", "第一季度", "count", quarterlyBugCount[0]));
            repairBugList.add(Map.of("name", "第二季度", "count", quarterlyBugCount[1]));
            repairBugList.add(Map.of("name", "第三季度", "count", quarterlyBugCount[2]));
            repairBugList.add(Map.of("name", "第四季度", "count", quarterlyBugCount[3]));
            

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
            // 从数据库获取月度发布数据
            Map<String, Object> data = new HashMap<>();
            
            // 月份列表
            List<String> months = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月");
            
            // 月度发布次数
            int[] releaseCounts = new int[12]; // 12个月
            
            // 获取指定年份
            int targetYear = Integer.parseInt(year);
            
            // 统计月度发布数据
            Iterable<Project> projects = projectService.findAll();
            Calendar cal = Calendar.getInstance();
            for (Project project : projects) {
                if (project.getStatus() != null && project.getStatus() == 2) { // 假设2表示已完成
                    if (project.getEnd_date() != null) {
                        cal.setTime(project.getEnd_date());
                        if (cal.get(Calendar.YEAR) == targetYear) {
                            int month = cal.get(Calendar.MONTH); // 月份从0开始
                            if (month >= 0 && month < 12) {
                                releaseCounts[month]++;
                            }
                        }
                    }
                }
            }
            
            // 转换为列表
            List<Integer> releaseCountsList = new ArrayList<>();
            for (int count : releaseCounts) {
                releaseCountsList.add(count);
            }
            
            data.put("months", months);
            data.put("releaseCounts", releaseCountsList);
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
            // 从数据库获取年度发布榜数据
            Map<String, Object> data = new HashMap<>();
            
            // 产品列表
            List<String> products = new ArrayList<>();
            // 发布次数列表
            List<Integer> releaseCounts = new ArrayList<>();
            
            // 获取指定年份
            int targetYear = Integer.parseInt(year);
            
            // 统计产品发布数据
            Map<String, Integer> productReleaseCount = new HashMap<>();
            Iterable<Project> projects = projectService.findAll();
            Calendar cal = Calendar.getInstance();
            for (Project project : projects) {
                if (project.getStatus() != null && project.getStatus() == 2) { // 假设2表示已完成
                    if (project.getEnd_date() != null) {
                        cal.setTime(project.getEnd_date());
                        if (cal.get(Calendar.YEAR) == targetYear) {
                            if (project.getName() != null) {
                                productReleaseCount.put(project.getName(), productReleaseCount.getOrDefault(project.getName(), 0) + 1);
                            }
                        }
                    }
                }
            }
            
            // 转换为列表并排序
            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(productReleaseCount.entrySet());
            entryList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
            
            // 构建发布榜数据
            for (Map.Entry<String, Integer> entry : entryList) {
                products.add(entry.getKey());
                releaseCounts.add(entry.getValue());
            }
            
            data.put("products", products);
            data.put("releaseCounts", releaseCounts);
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取年度发布榜数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取团队完成情况", description = "返回团队完成情况数据")
    @GetMapping("/team-statistics")
    public Result getTeamStatistics(@RequestParam(required = false) String projectName) {
        try {
            // 从数据库获取团队完成情况数据
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取今天和昨天的日期
            Calendar cal = Calendar.getInstance();
            Date todayDate = cal.getTime();
            cal.add(Calendar.DATE, -1);
            Date yesterdayDate = cal.getTime();
            
            // 格式化日期为yyyy-MM-dd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String todayStr = sdf.format(todayDate);
            String yesterdayStr = sdf.format(yesterdayDate);
            
            // 从数据库获取任务数据
            List<Task> tasks = taskService.findall();
            // 从数据库获取Bug数据
            List<Bug> bugs = bugService.findall();
            // 从数据库获取操作日志数据（用于统计创建需求数量）
            List<OperationLog> operationLogs = operationLogService.findall();
            
            // 如果指定了项目名称，查找对应的项目ID
            Long targetProjectId = null;
            if (projectName != null && !projectName.isEmpty()) {
                // 查找项目ID
                Iterable<Project> projects = projectService.findAll();
                for (Project project : projects) {
                    if (projectName.equals(project.getName())) {
                        targetProjectId = project.getId();
                        break;
                    }
                }
            }
            
            // 统计昨天的数据
            Map<String, Object> yesterday = new HashMap<>();
            int yesterdayTaskCount = 0; // 完成任务数量
            int yesterdayCreateCount = 0; // 创建需求数量
            int yesterdayTiChuCount = 0; // 提出Bug数量
            int yesterdayBugCount = 0; // 修改Bug数量
            int yesterdayClock = 0; // 总消耗工时
            
            // 统计今天的数据
            Map<String, Object> today = new HashMap<>();
            int todayTaskCount = 0; // 完成任务数量
            int todayCreateCount = 0; // 创建需求数量
            int todayTiChuCount = 0; // 提出Bug数量
            int todayBugCount = 0; // 修改Bug数量
            int todayClock = 0; // 总消耗工时
            
            // 统计任务数据
            for (Task task : tasks) {
                // 如果指定了项目，只统计该项目的任务
                if (targetProjectId != null) {
                    if (task.getProjectId() == null || !task.getProjectId().equals(targetProjectId.intValue())) {
                        continue;
                    }
                }
                
                if (task.getActualHours() != null) {
                    // 计算工时
                    if (task.getCreatedAt() != null) {
                        try {
                            // 直接比较字符串，假设createdAt格式为yyyy-MM-dd
                            String taskDateStr = task.getCreatedAt().substring(0, Math.min(10, task.getCreatedAt().length()));
                            if (taskDateStr.equals(yesterdayStr)) {
                                yesterdayClock += task.getActualHours();
                            } else if (taskDateStr.equals(todayStr)) {
                                todayClock += task.getActualHours();
                            }
                        } catch (Exception e) {
                            // 解析失败，跳过
                        }
                    }
                    
                    // 统计完成的任务
                    if (task.getStatus() != null && task.getStatus() == 3) { // 假设3表示已完成
                        if (task.getCreatedAt() != null) {
                            try {
                                // 直接比较字符串，假设createdAt格式为yyyy-MM-dd
                                String taskDateStr = task.getCreatedAt().substring(0, Math.min(10, task.getCreatedAt().length()));
                                if (taskDateStr.equals(yesterdayStr)) {
                                    yesterdayTaskCount++;
                                } else if (taskDateStr.equals(todayStr)) {
                                    todayTaskCount++;
                                }
                            } catch (Exception e) {
                                // 解析失败，跳过
                            }
                        }
                    }
                }
            }
            
            // 统计Bug数据
            for (Bug bug : bugs) {
                // 如果指定了项目，只统计该项目的Bug
                if (targetProjectId != null) {
                    if (bug.getProjectId() == null || !bug.getProjectId().equals(targetProjectId.intValue())) {
                        continue;
                    }
                }
                
                // 统计提出的Bug
                if (bug.getCreatedAt() != null) {
                    try {
                        // 直接比较字符串，假设createdAt格式为yyyy-MM-dd
                        String bugDate = bug.getCreatedAt().substring(0, Math.min(10, bug.getCreatedAt().length()));
                        if (bugDate.equals(yesterdayStr)) {
                            yesterdayTiChuCount++;
                        } else if (bugDate.equals(todayStr)) {
                            todayTiChuCount++;
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }
                
                // 统计修改的Bug
                if (bug.getStatus() != null && bug.getStatus() == 2) { // 假设2表示已解决
                    if (bug.getResolvedAt() != null) {
                        try {
                            // 直接比较字符串，假设resolvedAt格式为yyyy-MM-dd
                            String bugDate = bug.getResolvedAt().substring(0, Math.min(10, bug.getResolvedAt().length()));
                            if (bugDate.equals(yesterdayStr)) {
                                yesterdayBugCount++;
                            } else if (bugDate.equals(todayStr)) {
                                todayBugCount++;
                            }
                        } catch (Exception e) {
                            // 解析失败，跳过
                        }
                    }
                }
            }
            
            // 统计创建需求数量（从操作日志中）
            for (OperationLog log : operationLogs) {
                if (log.getAction() != null && log.getAction().contains("创建需求")) {
                    // 如果指定了项目，只统计该项目的需求创建
                    if (targetProjectId != null) {
                        // 这里需要根据实际情况判断操作日志是否与项目相关
                        // 假设操作日志中有projectId字段或者可以通过其他方式关联到项目
                        // 暂时跳过项目过滤，因为操作日志可能没有直接关联到项目
                    }
                    
                    if (log.getCreated_at() != null) {
                        String logDate = sdf.format(log.getCreated_at());
                        if (logDate.equals(yesterdayStr)) {
                            yesterdayCreateCount++;
                        } else if (logDate.equals(todayStr)) {
                            todayCreateCount++;
                        }
                    }
                }
            }
            
            // 计算平均消耗工时
            int yesterdayAverageClock = 0;
            if (yesterdayTaskCount > 0) {
                yesterdayAverageClock = yesterdayClock / yesterdayTaskCount;
            }
            
            int todayAverageClock = 0;
            if (todayTaskCount > 0) {
                todayAverageClock = todayClock / todayTaskCount;
            }
            
            // 填充昨天的数据
            yesterday.put("task", yesterdayTaskCount);
            yesterday.put("create", yesterdayCreateCount);
            yesterday.put("tiChu", yesterdayTiChuCount);
            yesterday.put("bug", yesterdayBugCount);
            yesterday.put("clock", yesterdayClock);
            yesterday.put("averageClock", yesterdayAverageClock);
            
            // 填充今天的数据
            today.put("task", todayTaskCount);
            today.put("create", todayCreateCount);
            today.put("tiChu", todayTiChuCount);
            today.put("bug", todayBugCount);
            today.put("clock", todayClock);
            today.put("averageClock", todayAverageClock);
            
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
                    if (bug.getProjectId() != null && bug.getProjectId().equals(projectId.intValue())) {
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
    public Result getTaskOverview(@RequestParam(required = false) String username) {
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
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            if (username != null) {
                var user = userService.findById(username);
                if (user.isPresent()) {
                    try {
                        Long userId = Long.parseLong(username);
                        List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                        for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                            if (member.getProjectId() != null) {
                                userProjectIds.add(member.getProjectId());
                            }
                        }
                    } catch (NumberFormatException e) {
                        // 用户名不是数字格式，返回空列表
                    }
                }
            }
            
            List<Task> tasks = taskService.findall();
            for (Task task : tasks) {
                // 如果指定了用户名，只统计与用户参与的项目相关的任务
                if (username != null) {
                    if (task.getProjectId() != null) {
                        Long projectId = task.getProjectId().longValue();
                        if (!userProjectIds.contains(projectId)) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                
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
    private RolePermissionUtils rolePermissionUtils;
    
    @Operation(summary = "获取工作台统计数据", description = "返回工作台统计数据")
    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam String username) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 1. 获取用户角色
            boolean isProductManager = rolePermissionUtils.isProductManager(username);
            boolean isDeveloper = rolePermissionUtils.isDeveloper(username);
            boolean isTester = rolePermissionUtils.isTester(username);
            boolean isAdmin = rolePermissionUtils.isAdmin(username);
            
            // 2. 获取待审批数（只有产品经理和管理员可以看到）
            int approveState = 0;
            if (isProductManager || isAdmin) {
                List<ProjectApproval> approvals = projectApprovalService.findall();
                approveState = approvals.size();
            }
            
            // 3. 获取任务数
            int taskState = 0;
            List<Task> tasks = taskService.findall();
            for (Task task : tasks) {
                if (isAdmin || isProductManager) {
                    // 管理员和产品经理可以看到所有任务
                    taskState++;
                } else if (isDeveloper || isTester) {
                    // 开发者和测试者只能看到自己的任务
                    if (task.getAssigneeId() != null && task.getAssigneeId().toString().equals(username)) {
                        taskState++;
                    }
                }
            }
            
            // 4. 获取Bug数
            int bugState = 0;
            List<Bug> bugs = bugService.findall();
            for (Bug bug : bugs) {
                if (isAdmin || isProductManager) {
                    // 管理员和产品经理可以看到所有Bug
                    bugState++;
                } else if (isDeveloper) {
                    // 开发者只能看到指派给自己的Bug
                    if (bug.getAssigneeId() != null && bug.getAssigneeId().toString().equals(username)) {
                        bugState++;
                    }
                } else if (isTester) {
                    // 测试者可以看到所有Bug（因为测试者需要测试和验证Bug）
                    bugState++;
                }
            }
            
            // 5. 获取文档数（暂时模拟）
            int passageState = 0;
            
            // 6. 昨天处理任务和Bug的次数（暂时模拟）
            int bug = 0;
            
            // 7. 添加用户角色信息
            statistics.put("role", rolePermissionUtils.getRoleName(username));
            
            // 构建返回数据
            statistics.put("bug", bugState);
            statistics.put("approveState", approveState);
            statistics.put("taskState", taskState);
            statistics.put("bugState", bugState);
            statistics.put("passageState", passageState);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取工作台统计数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取未关闭产品统计数据", description = "返回未关闭产品统计数据")
    @GetMapping("/product-statistics")
    public Result getProductStatistics(@RequestParam(required = false) String productName) {
        try {
            // 从数据库获取未关闭产品统计数据
            Map<String, Object> statistics = new HashMap<>();
            
            // 只有当没有指定产品名称时，才返回产品列表
            if (productName == null || productName.isEmpty()) {
                // 产品列表
                List<Map<String, Object>> productList = new ArrayList<>();
                Iterable<Product> products = productService.findAll();
                for (Product product : products) {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    productList.add(productMap);
                }
                statistics.put("productList", productList);
            }
            
            // 初始化统计数据
            int validNeeds = 0; // 有效需求数
            int deliveredNeeds = 0; // 已交付需求数
            int unclosedNeeds = 0; // 未关闭需求数
            int monthFinish = 0; // 本月完成需求数
            int monthAdd = 0; // 本月新增需求数
            double deliveryRate = 0; // 需求交付率
            
            // 获取当前月份
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int currentMonth = cal.get(Calendar.MONTH) + 1; // 月份从0开始，所以加1
            
            // 如果指定了产品名称，返回该产品的统计数据
            if (productName != null && !productName.isEmpty()) {
                // 首先尝试查找对应的产品
                Product targetProduct = null;
                Iterable<Product> products = productService.findAll();
                for (Product product : products) {
                    if (product.getName().equals(productName)) {
                        targetProduct = product;
                        break;
                    }
                }
                
                // 如果没有找到产品，尝试根据项目名称找到对应的产品
                if (targetProduct == null) {
                    Iterable<Project> projects = projectService.findAll();
                    for (Project project : projects) {
                        if (project.getName().equals(productName)) {
                            if (project.getProduct_id() != null) {
                                // 根据产品ID查找产品
                                for (Product product : products) {
                                    if (product.getId().equals(project.getProduct_id())) {
                                        targetProduct = product;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                
                if (targetProduct != null) {
                    // 查找该产品下的所有项目
                    Iterable<Project> projects = projectService.findAll();
                    List<Project> productProjects = new ArrayList<>();
                    for (Project project : projects) {
                        if (project.getProduct_id() != null && project.getProduct_id().equals(targetProduct.getId())) {
                            productProjects.add(project);
                        }
                    }
                }
            }
            
            // 计算需求交付率
            if (validNeeds > 0) {
                deliveryRate = (double) deliveredNeeds / validNeeds * 100;
            }
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未关闭产品统计数据失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取项目详细信息", description = "返回项目详细信息，包括开始时间、所属产品、团队成员及其职位等")
    @GetMapping("/project-info")
    public Result getProjectInfo(@RequestParam String projectName) {
        try {
            // 从数据库获取项目详细信息
            Map<String, Object> projectInfo = new HashMap<>();
            
            // 根据项目名称查找项目
            Project project = null;
            Iterable<Project> projects = projectService.findAll();
            for (Project p : projects) {
                if (projectName.equals(p.getName())) {
                    project = p;
                    break;
                }
            }
            
            if (project != null) {
                // 项目基本信息
                projectInfo.put("projectName", project.getName());
                projectInfo.put("startTime", project.getStart_date() != null ? project.getStart_date().toString() : "");
                projectInfo.put("finishTime", project.getEnd_date() != null ? project.getEnd_date().toString() : "");
                
                // 计算剩余时间
                if (project.getEnd_date() != null) {
                    Calendar cal = Calendar.getInstance();
                    Date today = cal.getTime();
                    long diff = project.getEnd_date().getTime() - today.getTime();
                    long days = diff / (1000 * 60 * 60 * 24);
                    projectInfo.put("remainingTime", days + "天");
                } else {
                    projectInfo.put("remainingTime", "未知");
                }
                
                // 项目风险（暂时设置为0，实际项目中应该从数据库获取）
                projectInfo.put("risk", 0);
                
                // 所属产品
                if (project.getProduct_id() != null) {
                    Iterable<Product> products = productService.findAll();
                    for (Product product : products) {
                        if (product.getId().equals(project.getProduct_id())) {
                            projectInfo.put("productName", product.getName());
                            break;
                        }
                    }
                } else {
                    projectInfo.put("productName", "未知");
                }
                
                // 项目状态
                String status = "未知";
                if (project.getStatus() != null) {
                    switch (project.getStatus()) {
                        case 0: status = "未开始"; break;
                        case 1: status = "进行中"; break;
                        case 2: status = "已完成"; break;
                    }
                }
                projectInfo.put("projectStatus", status);
                
                // 项目进度
                projectInfo.put("projectProgress", project.getProgress() != null ? project.getProgress() + "%" : "0%");
                
                // 项目经理
                if (project.getManagerId() != null) {
                    Iterable<User> users = userService.findAll();
                    for (User user : users) {
                        if (user.getUsername() != null && user.getUsername().equals(project.getManagerId().toString())) {
                            projectInfo.put("projectManager", user.getName() != null ? user.getName() : user.getUsername());
                            break;
                        }
                    }
                } else {
                    projectInfo.put("projectManager", "未知");
                }
                
                // 团队成员及其职位（暂时模拟数据，实际项目中应该从数据库获取）
                List<Map<String, String>> teamMembers = new ArrayList<>();
                teamMembers.add(Map.of("name", "张三", "position", "项目经理"));
                teamMembers.add(Map.of("name", "李四", "position", "开发工程师"));
                teamMembers.add(Map.of("name", "王五", "position", "测试工程师"));
                projectInfo.put("teamMembers", teamMembers);
                
                // 项目详情
                projectInfo.put("description", "这是一个详细的项目描述，包含项目的目标、范围、功能等信息。");
                
                // 项目目标
            List<String> projectGoals = new ArrayList<>();
            projectGoals.add("完成智慧教室系统的开发");
            projectGoals.add("实现智慧云盘功能");
            projectGoals.add("确保系统稳定运行");
            projectInfo.put("projectGoals", projectGoals);
            
            // 团队名称
            projectInfo.put("teamName", "智慧教室开发团队");
            } else {
                // 如果没有找到项目，返回默认数据
                projectInfo.put("projectName", "暂无项目");
                projectInfo.put("startTime", "");
                projectInfo.put("finishTime", "");
                projectInfo.put("remainingTime", "");
                projectInfo.put("risk", 0);
                projectInfo.put("productName", "");
                projectInfo.put("projectStatus", "");
                projectInfo.put("projectProgress", "");
                projectInfo.put("projectManager", "");
                projectInfo.put("teamMembers", new ArrayList<>());
            projectInfo.put("description", "");
            projectInfo.put("projectGoals", new ArrayList<>());
            projectInfo.put("teamName", "");
            }
            
            return Result.success(projectInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目详细信息失败: " + e.getMessage());
        }
    }
}
