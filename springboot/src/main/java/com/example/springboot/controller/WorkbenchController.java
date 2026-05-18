package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.ProjectApproval;
import com.example.springboot.entity.ProjectMember;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.User;

import com.example.springboot.entity.Task;
import com.example.springboot.service.BugService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.UserService;
import com.example.springboot.service.ProjectMemberService;
import com.example.springboot.service.ProductService;
import com.example.springboot.service.OperationLogService;
import com.example.springboot.service.TeamMemberService;
import com.example.springboot.service.FeedbackService;
import com.example.springboot.service.TestSuiteService;
import com.example.springboot.utils.RolePermissionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.example.springboot.entity.OperationLog;
import com.example.springboot.service.OperationLogService;

@RestController
@RequestMapping("/workbench")
@Tag(name="工作台列表模块")
public class WorkbenchController {

    @Resource
    ProjectApprovalService projectApprovalService;
    @Resource
    TaskService taskService;

    @Resource
    BugService bugService;
    @Resource
    ProjectService projectService;
    @Resource
    UserService userService;
    @Resource
    ProjectMemberService projectMemberService;
    @Resource
    ProductService productService;
    @Resource
    RolePermissionUtils rolePermissionUtils;
    @Resource
    FeedbackService feedbackService;
    @Resource
    TestSuiteService testSuiteService;
    @Resource
    TeamMemberService teamMemberService;

    @Operation(summary = "获取审批列表", description = "返回审批列表数据")
    @GetMapping("/approvals")
    public Result getApprovals(@RequestParam(required = false) String username) {
        try {
            // 检查用户权限，只有产品经理和管理员可以访问审批表
            if (username != null && !username.isEmpty()) {
                boolean isProductManager = rolePermissionUtils.isProductManager(username);
                boolean isAdmin = rolePermissionUtils.isAdmin(username);
                
                if (!isProductManager && !isAdmin) {
                    // 不是产品经理和管理员，返回空列表
                    return Result.success(new ArrayList<>());
                }
            }
            
            // 从数据库获取实际的审批数据
            List<ProjectApproval> approvals = projectApprovalService.findall();
            List<Map<String, Object>> approvalList = new ArrayList<>();
            
            // 遍历审批数据，转换为前端需要的格式
            for (ProjectApproval approval : approvals) {
                Map<String, Object> approvalMap = new HashMap<>();
                approvalMap.put("id", approval.getId());
                
                // 获取项目名称
                String projectName = "未知项目";
                if (approval.getProject_id() != null) {
                    try {
                        var project = projectService.findById(approval.getProject_id().longValue());
                        if (project.isPresent()) {
                            projectName = project.get().getName();
                        }
                    } catch (Exception e) {
                        System.out.println("获取项目名称失败: " + e.getMessage());
                    }
                }
                approvalMap.put("project_name", projectName);
                
                // 获取审批内容
                approvalMap.put("comment", approval.getComment() != null ? approval.getComment() : "");
                
                // 获取审批类型
                String approvalType = approval.getType() != null ? approval.getType() : "未知类型";
                approvalMap.put("type", approvalType);
                
                // 获取审批人姓名
                String approverName = "未知审批人";
                if (approval.getApprover_id() != null) {
                    try {
                        var user = userService.findById(approval.getApprover_id().toString());
                        if (user.isPresent()) {
                            approverName = user.get().getName();
                        }
                    } catch (Exception e) {
                        System.out.println("获取审批人姓名失败: " + e.getMessage());
                    }
                }
                approvalMap.put("approver_name", approverName);
                
                // 获取提交时间
                String createdAt = approval.getCreated_at() != null ? approval.getCreated_at() : "";
                approvalMap.put("created_at", createdAt);
                
                // 获取审批状态
                String action = approval.getAction() != null ? approval.getAction() : "待审批";
                approvalMap.put("action", action);
                
                // 添加反馈ID，用于审批操作后同步更新反馈状态
                approvalMap.put("feedback_id", approval.getFeedback_id());
                
                approvalList.add(approvalMap);
            }
            
            System.out.println("返回的审批列表数量: " + approvalList.size());
            return Result.success(approvalList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取审批列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据反馈ID删除审批记录", description = "当反馈被删除或关闭时，删除相关的审批记录")
    @DeleteMapping("/approvals/delete-by-feedback/{feedbackId}")
    public Result deleteApprovalByFeedbackId(@PathVariable Long feedbackId) {
        try {
            System.out.println("删除与反馈相关的审批记录，反馈ID: " + feedbackId);
            projectApprovalService.deleteByFeedbackId(feedbackId);
            System.out.println("删除与反馈相关的审批记录成功");
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除审批记录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取任务列表", description = "返回任务列表数据")
    @GetMapping("/tasks")
    public Result getTasks(@RequestParam(required = false) String username) {
        try {
            System.out.println("开始获取任务列表，用户名: " + username);
            List<Task> tasks = taskService.findall();
            System.out.println("获取到的任务数量: " + tasks.size());
            List<Map<String, Object>> taskList = new ArrayList<>();
            
            // 获取用户管理的产品项目ID列表（如果是产品经理）
            Set<Long> userManagedProjectIds = new HashSet<>();
            boolean isProductManager = false;
            
            if (username != null && !username.isEmpty()) {
                try {
                    System.out.println("开始获取用户信息，用户名: " + username);
                    var user = userService.findByUsername(username);
                    System.out.println("获取到的用户信息: " + (user != null ? user.getName() : "null"));
                    if (user != null) {
                        System.out.println("用户角色id: " + user.getRole_id());
                        // 假设role_id为1或2表示不需要筛选的角色
                        if (user.getRole_id() != null) {
                            System.out.println("用户角色id不为null，值为: " + user.getRole_id());
                            if (user.getRole_id().equals(1L) || user.getRole_id().equals(2L)) {
                                isProductManager = true;
                                System.out.println("用户角色id为" + user.getRole_id() + "，获取管理的产品项目下的任务");
                                
                                // 如果是产品经理，获取自己管理的产品下的所有项目ID
                                Iterable<Product> allProducts = productService.findAll();
                                for (Product product : allProducts) {
                                    // 检查产品是否由该用户负责
                                    if (product.getOwner_id() != null && 
                                        (product.getOwner_id().toString().equals(username) || 
                                         (user.getId() != null && product.getOwner_id().equals(user.getId().longValue())))) {
                                        // 找到该产品下的所有项目
                                        Iterable<Project> allProjects = projectService.findAll();
                                        for (Project project : allProjects) {
                                            if (project.getProduct_id() != null && project.getProduct_id().equals(product.getId())) {
                                                userManagedProjectIds.add(project.getId());
                                                System.out.println("产品经理管理的产品[" + product.getName() + "]下的项目: " + project.getName());
                                            }
                                        }
                                    }
                                }
                                System.out.println("产品经理管理的项目ID列表: " + userManagedProjectIds);
                            } else {
                                System.out.println("用户角色id为" + user.getRole_id() + "，需要筛选任务");
                            }
                        } else {
                            System.out.println("用户角色id为null，需要筛选任务");
                        }
                    } else {
                        System.out.println("未找到用户信息");
                    }
                } catch (Exception e) {
                    System.out.println("获取用户信息失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            for (Task task : tasks) {
                System.out.println("处理任务: " + task.getTitle() + "，负责人ID: " + task.getAssigneeId() + "，项目ID: " + task.getProjectId());
                // 如果指定了用户名
                if (username != null && !username.isEmpty()) {
                    try {
                        Integer userId = Integer.parseInt(username);
                        System.out.println("转换后的用户ID: " + userId);
                        
                        // 如果是产品经理
                        if (isProductManager) {
                            // 产品经理只看到自己管理产品项目下的任务
                            if (task.getProjectId() != null && userManagedProjectIds.contains(task.getProjectId().longValue())) {
                                System.out.println("产品经理看到管理项目下的任务: " + task.getTitle());
                            } else if (task.getProjectId() == null) {
                                // projectId为null的任务也显示
                                System.out.println("产品经理看到无项目任务: " + task.getTitle());
                            } else {
                                System.out.println("跳过任务: " + task.getTitle() + "，不在管理项目下");
                                continue; // 跳过不在管理项目下的任务
                            }
                        } else {
                            // 非产品经理，只返回被指派给当前用户的任务
                            if (task.getAssigneeId() == null || !task.getAssigneeId().equals(userId)) {
                                System.out.println("跳过任务: " + task.getTitle() + "，负责人ID: " + task.getAssigneeId() + "，当前用户ID: " + userId);
                                continue; // 跳过不是当前用户的任务
                            }
                        }
                    } catch (NumberFormatException e) {
                        // 用户名不是数字格式，如果是产品经理，仍然显示管理项目下的任务
                        System.out.println("用户名不是数字格式: " + e.getMessage());
                        if (!isProductManager) {
                            System.out.println("非产品经理，跳过任务");
                            continue; // 非产品经理，跳过
                        }
                    }
                }
                
                try {
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("id", task.getId());
                    taskMap.put("title", task.getTitle());
                    taskMap.put("description", task.getDescription());
                    taskMap.put("project_id", task.getProjectId());
                    taskMap.put("requirement_id", task.getRequirementId());
                    taskMap.put("parent_id", task.getParentId());
                    taskMap.put("creator_id", task.getCreatorId());
                    taskMap.put("assignee_id", task.getAssigneeId());
                    
                    // 添加负责人姓名
                    String assigneeName = "未指派";
                    if (task.getAssigneeId() != null) {
                        try {
                            System.out.println("获取负责人信息，ID: " + task.getAssigneeId());
                            Optional<User> userOpt = userService.findById(task.getAssigneeId().toString());
                            if (userOpt.isPresent()) {
                                assigneeName = userOpt.get().getName() != null ? userOpt.get().getName() : userOpt.get().getUsername();
                                System.out.println("负责人姓名: " + assigneeName);
                            } else {
                                System.out.println("未找到负责人信息");
                            }
                        } catch (Exception e) {
                            // 如果获取负责人信息失败，使用默认值
                            System.out.println("获取负责人信息失败: " + e.getMessage());
                        }
                    }
                    taskMap.put("assignee_name", assigneeName);
                    taskMap.put("priority", task.getPriority());
                    taskMap.put("status", task.getStatus());
                    taskMap.put("progress", task.getProgress());
                    taskMap.put("estimated_hours", task.getEstimatedHours());
                    taskMap.put("actual_hours", task.getActualHours());
                    taskMap.put("start_date", task.getStartDate());
                    taskMap.put("due_date", task.getDueDate());
                    taskMap.put("created_at", task.getCreatedAt());
                    taskMap.put("type", ""); // 默认值
                    taskMap.put("solution", ""); // 默认值
                    
                    // 获取项目名称
                    String projectName = "未关联项目";
                    Integer taskProjectId = task.getProjectId();
                    System.out.println("任务[" + task.getTitle() + "]的项目ID(Integer): " + (taskProjectId != null ? taskProjectId : "null"));
                    
                    if (taskProjectId != null) {
                        try {
                            Long projectIdLong = taskProjectId.longValue();
                            System.out.println("转换为Long类型: " + projectIdLong);
                            var project = projectService.findById(projectIdLong);
                            if (project.isPresent()) {
                                projectName = project.get().getName();
                                System.out.println("找到项目，名称: " + projectName + "，项目ID: " + project.get().getId());
                            } else {
                                System.out.println("WARNING: 项目ID=" + taskProjectId + " 在数据库中不存在");
                            }
                        } catch (Exception e) {
                            System.out.println("ERROR: 获取项目信息失败，项目ID=" + taskProjectId + ", 错误: " + e.getMessage());
                        }
                    } else {
                        System.out.println("任务[" + task.getTitle() + "]没有关联项目(project_id为空)");
                    }
                    taskMap.put("project_name", projectName);
                    
                    taskList.add(taskMap);
                    System.out.println("添加任务到列表: " + task.getTitle());
                } catch (Exception e) {
                    System.out.println("处理任务失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("返回的任务列表数量: " + taskList.size());
            return Result.success(taskList);
        } catch (Exception e) {
            System.out.println("获取任务列表失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取任务详情", description = "根据ID获取任务详情")
    @GetMapping("/tasks/{id}")
    public Result getTaskById(@PathVariable Integer id) {
        try {
            System.out.println("获取任务详情，ID: " + id);
            List<Task> tasks = taskService.findall();
            System.out.println("任务列表数量: " + tasks.size());
            for (Task task : tasks) {
                System.out.println("任务ID: " + task.getId() + ", 标题: " + task.getTitle());
                if (task.getId().equals(id)) {
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("id", task.getId());
                    taskMap.put("title", task.getTitle());
                    taskMap.put("name", task.getTitle()); // 为了前端兼容，同时返回name字段
                    taskMap.put("description", task.getDescription());
                    taskMap.put("project_id", task.getProjectId());
                    taskMap.put("requirement_id", task.getRequirementId());
                    taskMap.put("parent_id", task.getParentId());
                    taskMap.put("creator_id", task.getCreatorId());
                    taskMap.put("assignee_id", task.getAssigneeId());
                    taskMap.put("priority", task.getPriority());
                    taskMap.put("status", task.getStatus());
                    taskMap.put("progress", task.getProgress());
                    taskMap.put("estimated_hours", task.getEstimatedHours());
                    taskMap.put("actual_hours", task.getActualHours());
                    taskMap.put("start_date", task.getStartDate());
                    taskMap.put("due_date", task.getDueDate());
                    taskMap.put("created_at", task.getCreatedAt());
                    
                    // 获取项目名称
                    String projectName = "未知项目";
                    if (task.getProjectId() != null) {
                        try {
                            var project = projectService.findById(task.getProjectId().longValue());
                            if (project.isPresent()) {
                                projectName = project.get().getName();
                            }
                        } catch (Exception e) {
                            System.out.println("获取项目名称失败: " + e.getMessage());
                        }
                    }
                    taskMap.put("project_name", projectName);
                    
                    System.out.println("返回任务详情: " + taskMap);
                    return Result.success(taskMap);
                }
            }
            System.out.println("任务不存在，ID: " + id);
            return Result.error("任务不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }





    @Operation(summary = "获取Bug列表", description = "返回Bug列表数据")
    @GetMapping("/bugs")
    public Result getBugs(@RequestParam(required = false) String username) {
        try {
            System.out.println("开始获取Bug列表，用户名: " + username);
            List<Bug> bugs = bugService.findall();
            System.out.println("获取到的Bug数量: " + bugs.size());
            List<Map<String, Object>> bugList = new ArrayList<>();
            
            // 获取用户参与的项目ID列表（如果是产品经理）
            boolean isProductManager = false;
            Set<Long> teamMemberUserIds = new HashSet<>(); // 同团队成员的用户ID集合
            
            if (username != null && !username.isEmpty()) {
                try {
                    System.out.println("开始获取用户信息，用户名: " + username);
                    var user = userService.findByUsername(username);
                    System.out.println("获取到的用户信息: " + (user != null ? user.getName() : "null"));
                    if (user != null) {
                        System.out.println("用户角色id: " + user.getRole_id());
                        // 假设role_id为1或2表示不需要筛选的角色
                        if (user.getRole_id() != null) {
                            System.out.println("用户角色id不为null，值为: " + user.getRole_id());
                            if (user.getRole_id().equals(1L) || user.getRole_id().equals(2L)) {
                                isProductManager = true;
                                System.out.println("用户角色id为" + user.getRole_id() + "，返回所有Bug");
                            } else {
                                System.out.println("用户角色id为" + user.getRole_id() + "，需要筛选Bug");
                            }
                        } else {
                            System.out.println("用户角色id为null，需要筛选Bug");
                        }
                        
                        // 获取当前用户所在的所有团队，并收集同团队成员的用户ID
                        if (!isProductManager) {
                            try {
                                Long currentUserId = Long.parseLong(username);
                                System.out.println("当前用户ID: " + currentUserId);
                                
                                // 查找用户所在的所有团队
                                List<com.example.springboot.entity.TeamMember> userTeamMembers = teamMemberService.findByUserId(currentUserId);
                                System.out.println("用户所在的团队数量: " + userTeamMembers.size());
                                
                                for (com.example.springboot.entity.TeamMember tm : userTeamMembers) {
                                    System.out.println("团队ID: " + tm.getTeamId());
                                    // 查找该团队的所有成员
                                    List<com.example.springboot.entity.TeamMember> allTeamMembers = teamMemberService.findByTeamId(tm.getTeamId());
                                    System.out.println("团队成员数量: " + allTeamMembers.size());
                                    
                                    for (com.example.springboot.entity.TeamMember member : allTeamMembers) {
                                        if (member.getUserId() != null) {
                                            teamMemberUserIds.add(member.getUserId());
                                            System.out.println("添加团队成员ID: " + member.getUserId());
                                        }
                                    }
                                }
                                
                                System.out.println("同团队成员ID集合: " + teamMemberUserIds);
                            } catch (NumberFormatException e) {
                                System.out.println("用户名无法转换为数字: " + username);
                            }
                        }
                    } else {
                        System.out.println("未找到用户信息");
                    }
                } catch (Exception e) {
                    System.out.println("获取用户信息失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            for (Bug bug : bugs) {
                System.out.println("===== 处理Bug: " + bug.getTitle() + " =====");
                System.out.println("Bug标题: " + bug.getTitle() + "，负责人ID: " + bug.getAssigneeId() + "，负责人ID类型: " + (bug.getAssigneeId() != null ? bug.getAssigneeId().getClass().getName() : "null"));
                System.out.println("项目ID: " + bug.getProjectId() + "，状态: " + bug.getStatus());
                
                // 如果指定了用户名
                if (username != null && !username.isEmpty()) {
                    try {
                        Long userId = Long.parseLong(username);
                        System.out.println("当前登录用户: username=" + username + ", 转换后的用户ID=" + userId + ", 类型=" + userId.getClass().getName());
                        
                        // 如果是产品经理
                        if (isProductManager) {
                            // 产品经理可以看到所有Bug
                            System.out.println("用户是产品经理，显示Bug: " + bug.getTitle());
                        } else {
                            // 非产品经理，显示同团队内的Bug
                            System.out.println("用户不是产品经理，检查是否是同团队内的Bug");
                            
                            boolean shouldShow = false;
                            String reason = "";
                            
                            // 检查是否指派给自己
                            if (bug.getAssigneeId() != null && bug.getAssigneeId().equals(userId)) {
                                shouldShow = true;
                                reason = "指派给自己";
                            }
                            // 检查是否是自己创建的
                            else if (bug.getReporterId() != null && bug.getReporterId().equals(userId)) {
                                shouldShow = true;
                                reason = "自己创建的";
                            }
                            // 检查是否指派给同团队成员
                            else if (bug.getAssigneeId() != null && teamMemberUserIds.contains(bug.getAssigneeId())) {
                                shouldShow = true;
                                reason = "指派给同团队成员";
                            }
                            // 检查是否是同团队成员创建的
                            else if (bug.getReporterId() != null && teamMemberUserIds.contains(bug.getReporterId())) {
                                shouldShow = true;
                                reason = "同团队成员创建的";
                            }
                            
                            if (!shouldShow) {
                                System.out.println("跳过Bug: " + bug.getTitle() + "，不属于同团队内的Bug");
                                continue;
                            } else {
                                System.out.println("保留Bug: " + bug.getTitle() + "，原因: " + reason);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // 用户名不是数字格式，如果是产品经理，仍然显示所有Bug
                        System.out.println("用户名不是数字格式: " + e.getMessage());
                        if (!isProductManager) {
                            System.out.println("非产品经理，跳过Bug");
                            continue; // 非产品经理，跳过
                        }
                    }
                }
                
                try {
                    Map<String, Object> bugMap = new HashMap<>();
                    bugMap.put("id", bug.getId());
                    bugMap.put("title", bug.getTitle());
                    bugMap.put("description", bug.getDescription());
                    bugMap.put("projectId", bug.getProjectId());
                    bugMap.put("reporterId", bug.getReporterId());
                    bugMap.put("assigneeId", bug.getAssigneeId());
                    bugMap.put("severity", bug.getSeverity());
                    bugMap.put("status", bug.getStatus());
                    bugMap.put("bugType", bug.getBugType());
                    bugMap.put("createdAt", bug.getCreatedAt());
                    bugMap.put("resolvedAt", bug.getResolvedAt());
                    
                    // 添加负责人姓名
                    String assigneeName = "未指派";
                    if (bug.getAssigneeId() != null) {
                        try {
                            System.out.println("Bug ID: " + bug.getId() + "，负责人ID: " + bug.getAssigneeId() + "，类型: " + bug.getAssigneeId().getClass());
                            // 直接使用Integer类型的assigneeId查找用户
                            Optional<User> userOpt = userService.findById(bug.getAssigneeId().toString());
                            System.out.println("查找用户结果: " + (userOpt.isPresent() ? "找到" : "未找到"));
                            if (userOpt.isPresent()) {
                                User user = userOpt.get();
                                System.out.println("用户信息: ID=" + user.getId() + "，username=" + user.getUsername() + ", 姓名=" + user.getName());
                                assigneeName = user.getName() != null ? user.getName() : user.getUsername();
                                System.out.println("负责人姓名: " + assigneeName);
                            } else {
                                // 如果没有找到用户，尝试遍历所有用户，查看是否有匹配的
                                System.out.println("未找到用户，开始遍历所有用户:");
                                Iterable<User> allUsers = userService.findAll();
                                for (User user : allUsers) {
                                    System.out.println("用户: ID=" + user.getId() + "，username=" + user.getUsername() + ", 姓名=" + user.getName());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("获取负责人信息失败: " + e.getMessage());
                            e.printStackTrace();
                            // 如果获取负责人信息失败，使用默认值
                        }
                    } else {
                        System.out.println("Bug ID: " + bug.getId() + "，负责人ID为null");
                    }
                    bugMap.put("assignee_name", assigneeName);
                    
                    // 添加创建人姓名
                    String creatorName = "未知";
                    if (bug.getReporterId() != null) {
                        try {
                            Optional<User> reporterOpt = userService.findById(bug.getReporterId().toString());
                            if (reporterOpt.isPresent()) {
                                User reporter = reporterOpt.get();
                                creatorName = reporter.getName() != null ? reporter.getName() : reporter.getUsername();
                            }
                        } catch (Exception e) {
                            System.out.println("获取创建人信息失败: " + e.getMessage());
                        }
                    }
                    bugMap.put("creator_name", creatorName);
                    
                    // 获取项目名称
                    String projectName = "未知项目";
                    if (bug.getProjectId() != null) {
                        try {
                            System.out.println("获取项目信息，ID: " + bug.getProjectId());
                            var project = projectService.findById(bug.getProjectId().longValue());
                            if (project.isPresent()) {
                                projectName = project.get().getName();
                                System.out.println("项目名称: " + projectName);
                            } else {
                                System.out.println("未找到项目信息");
                            }
                        } catch (Exception e) {
                            System.out.println("获取项目名称失败: " + e.getMessage());
                        }
                    }
                    bugMap.put("project_name", projectName);
                    
                    bugList.add(bugMap);
                    System.out.println("添加Bug到列表: " + bug.getTitle());
                } catch (Exception e) {
                    System.out.println("处理Bug失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("返回的Bug列表数量: " + bugList.size());
            return Result.success(bugList);
        } catch (Exception e) {
            System.out.println("获取Bug列表失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取Bug列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目列表", description = "返回项目列表数据")
    @GetMapping("/projects")
    public Result getProjects(@RequestParam String username) {
        try {
            System.out.println("========== 获取项目列表开始 ==========");
            System.out.println("请求用户名: " + username);
            
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            // 获取用户ID
            System.out.println("1. 查找用户信息...");
            var user = userService.findById(username);
            if (user.isPresent()) {
                Long userId = Long.parseLong(user.get().getUsername());
                System.out.println("   用户ID: " + userId);
                System.out.println("   用户姓名: " + user.get().getName());
                
                // 根据用户ID查询参与的项目成员记录（所有角色都只显示自己参与的项目）
                System.out.println("2. 查询项目成员记录...");
                List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                System.out.println("   获取到的项目成员记录数量: " + projectMembers.size());
                
                if (projectMembers.isEmpty()) {
                    System.out.println("   警告: 用户在项目成员表中没有记录");
                    System.out.println("   请检查：1) 用户是否被添加到项目成员表 2) 项目创建时是否自动添加负责人");
                }
                
                // 遍历项目成员记录，获取对应的项目信息
                for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                    Long projectId = member.getProjectId();
                    System.out.println("3. 处理项目成员记录:");
                    System.out.println("   项目ID: " + projectId);
                    System.out.println("   用户ID: " + member.getUserId());
                    System.out.println("   角色类型: " + member.getRoleType());
                    
                    if (projectId != null) {
                        var project = projectService.findById(projectId);
                        System.out.println("   获取到的项目信息: " + (project.isPresent() ? project.get().getName() : "不存在"));
                        if (project.isPresent()) {
                            Project p = project.get();
                            Map<String, Object> projectMap = new HashMap<>();
                            projectMap.put("id", p.getId());
                            projectMap.put("projectName", p.getName());
                            
                            // 从数据库查询项目成员数
                            List<com.example.springboot.entity.ProjectMember> projectMemberList = projectMemberService.findByProjectId(projectId);
                            projectMap.put("projectMember", projectMemberList.size());
                            
                            projectMap.put("finishTime", p.getEnd_date());
                            projectMap.put("degree", p.getProgress() != null ? p.getProgress() : 0);
                            projectMap.put("status", p.getStatus() != null ? p.getStatus() : 0);
                            System.out.println("   项目进度: " + (p.getProgress() != null ? p.getProgress() : 0));
                            System.out.println("   项目状态: " + (p.getStatus() != null ? p.getStatus() : 0));
                            projectList.add(projectMap);
                        } else {
                            System.out.println("   警告: 项目不存在，项目ID: " + projectId);
                        }
                    } else {
                        System.out.println("   警告: 项目ID为空");
                    }
                }
            } else {
                System.out.println("警告: 未找到用户信息，用户名: " + username);
            }
            
            System.out.println("4. 返回结果:");
            System.out.println("   返回的项目列表数量: " + projectList.size());
            System.out.println("========== 获取项目列表结束 ==========");
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取未完成项目列表", description = "返回未完成项目列表数据")
    @GetMapping("/unfinished-projects")
    public Result getUnfinishedProjects(@RequestParam(required = false) String username) {
        try {
            System.out.println("========== 获取未完成项目列表开始，用户: " + username);
            
            // 获取所有项目
            Iterable<Project> allProjects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            // 检查用户是否是管理员或产品经理
            boolean isAdmin = false;
            boolean isProductManager = false;
            if (username != null && !username.isEmpty()) {
                isAdmin = rolePermissionUtils.isAdmin(username);
                isProductManager = rolePermissionUtils.isProductManager(username);
                System.out.println("用户 " + username + ", 是管理员: " + isAdmin + ", 是产品经理: " + isProductManager);
            }
            
            // 如果没有提供用户名，或者用户是管理员或产品经理，返回所有未完成项目
            if (username == null || username.isEmpty() || isAdmin || isProductManager) {
                System.out.println("没有提供用户名，或用户是管理员/产品经理，返回所有未完成项目");
                for (Project project : allProjects) {
                    if (project.getStatus() == null || (project.getStatus() != 2 && project.getStatus() != 3)) {
                        System.out.println("  添加项目: " + project.getName());
                        Map<String, Object> projectMap = buildProjectMapWithDetails(project);
                        projectList.add(projectMap);
                    }
                }
                return Result.success(projectList);
            }
            
            // 获取用户ID（用户名字段）
            Long userUsername = null;
            try {
                userUsername = Long.parseLong(username);
            } catch (NumberFormatException e) {
                System.out.println("用户名字段格式错误: " + username);
                return Result.success(projectList);
            }
            
            // 从项目成员表中查找用户参与的项目ID
            List<Long> userProjectIds = new ArrayList<>();
            List<ProjectMember> projectMembers = projectMemberService.findByUserId(userUsername);
            System.out.println("用户 " + username + " 在项目成员表中的记录数量: " + projectMembers.size());
            
            for (ProjectMember member : projectMembers) {
                if (member.getProjectId() != null) {
                    userProjectIds.add(member.getProjectId().longValue());
                    System.out.println("  找到项目ID: " + member.getProjectId());
                }
            }
            
            // 遍历所有项目，只返回用户参与的未完成项目
            for (Project project : allProjects) {
                // 过滤出未完成的项目（状态不是2已关闭或3已归档）
                if (project.getStatus() == null || (project.getStatus() != 2 && project.getStatus() != 3)) {
                    boolean hasAccess = userProjectIds.contains(project.getId());
                    System.out.println("检查项目: " + project.getName() + " (ID: " + project.getId() + "), 有权限: " + hasAccess);
                    
                    if (hasAccess) {
                        Map<String, Object> projectMap = new HashMap<>();
                        projectMap.put("title", project.getName());
                        
                        // 获取负责人姓名
                        String managerName = "未知"; 
                        if (project.getManagerId() != null) {
                            try {
                                var user = userService.findById(project.getManagerId().toString());
                                if (user.isPresent()) {
                                    managerName = user.get().getName();
                                }
                            } catch (Exception e) {
                                System.out.println("获取负责人姓名失败: " + e.getMessage());
                            }
                        }
                        projectMap.put("person", managerName);
                        
                        // 根据状态设置显示文本
                        String statusText = "已排期"; // 默认状态
                        if (project.getStatus() != null) {
                            switch (project.getStatus()) {
                                case 0:
                                    statusText = "未开始";
                                    break;
                                case 1:
                                    statusText = "进行中";
                                    break;
                            }
                        }
                        projectMap.put("states", statusText);
                        
                        // 从数据库获取实际数据（保持Long类型以正确匹配）
                        Long projectId = project.getId();
                        System.out.println("项目 " + project.getName() + " 的ID: " + projectId);
                        
                        // 计算总工时（从tasks表中获取）
                        int totalHours = 0;
                        List<Task> tasks = taskService.findall();
                        System.out.println("  总任务数: " + tasks.size());
                        for (Task task : tasks) {
                            if (task.getProjectId() != null) {
                                System.out.println("  任务: " + task.getTitle() + ", projectId=" + task.getProjectId() + ", status=" + task.getStatus());
                                if (task.getProjectId().longValue() == projectId) {
                                    if (task.getEstimatedHours() != null) {
                                        totalHours += task.getEstimatedHours().intValue();
                                    }
                                }
                            }
                        }
                        projectMap.put("workTime", totalHours + "h");
                        System.out.println("  总工时: " + totalHours + "h");
                        
                        // 计算剩余任务数量（从tasks表中获取）
                        int remainingTasks = 0;
                        for (Task task : tasks) {
                            if (task.getProjectId() != null && task.getProjectId().longValue() == projectId) {
                                // 假设任务状态为0或1表示未完成
                                if (task.getStatus() == null || task.getStatus() < 2) {
                                    remainingTasks++;
                                }
                            }
                        }
                        projectMap.put("shengYuTask", remainingTasks);
                        System.out.println("  剩余任务数: " + remainingTasks);
                        
                        // 计算剩余Bug数量（从bugs表中获取）
                        int remainingBugs = 0;
                        List<Bug> bugs = bugService.findall();
                        System.out.println("  总Bug数: " + bugs.size());
                        for (Bug bug : bugs) {
                            if (bug.getProjectId() != null) {
                                System.out.println("  Bug: " + bug.getTitle() + ", projectId=" + bug.getProjectId() + ", status=" + bug.getStatus());
                                if (bug.getProjectId().equals(projectId)) {
                                    // 假设Bug状态为0或1表示未解决
                                    if (bug.getStatus() == null || bug.getStatus() < 2) {
                                        remainingBugs++;
                                    }
                                }
                            }
                        }
                        projectMap.put("shengYuBug", remainingBugs);
                        System.out.println("  剩余Bug数: " + remainingBugs);
                        
                        // 计算未完成测试套件数量（从test_suites表中获取）
                        int remainingTestSuites = 0;
                        List<com.example.springboot.entity.TestSuite> testSuites = testSuiteService.findAll();
                        for (com.example.springboot.entity.TestSuite testSuite : testSuites) {
                            if (testSuite.getProject_id() != null && testSuite.getProject_id().equals(project.getId())) {
                                // 假设测试套件状态不为2表示未完成
                                if (testSuite.getStatus() == null || testSuite.getStatus() != 2) {
                                    remainingTestSuites++;
                                }
                            }
                        }
                        projectMap.put("remainingTestSuites", remainingTestSuites);
                        
                        // 设置完成时间和进度
                        projectMap.put("finishTime", project.getEnd_date());
                        projectMap.put("jinDu", project.getProgress() != null ? project.getProgress() : 0);
                        
                        // 判断项目是否有未完成的内容
                        boolean hasUnfinishedContent = remainingTasks > 0 || remainingBugs > 0 || remainingTestSuites > 0;
                        projectMap.put("hasUnfinishedContent", hasUnfinishedContent);
                        
                        // 添加到列表（所有状态为未完成的项目都显示）
                        projectList.add(projectMap);
                    }
                }
            }
            
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未完成项目列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目任务列表", description = "根据项目ID获取该项目下的所有任务，按创建时间倒序排序")
    @GetMapping("/project-tasks")
    public Result getProjectTasks(@RequestParam Long projectId) {
        try {
            System.out.println("========== 获取项目任务列表开始 ==========");
            System.out.println("请求的项目ID: " + projectId);
            
            if (projectId == null) {
                System.out.println("项目ID为空");
                System.out.println("========== 获取项目任务列表结束 ==========");
                return Result.success(new ArrayList<>());
            }
            
            // 根据项目ID获取任务列表
            List<Task> tasks = taskService.findall();
            System.out.println("获取到的任务总数: " + tasks.size());
            List<Map<String, Object>> taskList = new ArrayList<>();
            
            for (Task task : tasks) {
                Integer taskProjectId = task.getProjectId();
                String taskTitle = task.getTitle();
                System.out.println("检查任务: 标题='" + taskTitle + "', projectId=" + taskProjectId);
                
                if (taskProjectId != null && taskProjectId.equals(projectId.intValue())) {
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("id", task.getId());
                    taskMap.put("title", taskTitle);
                    taskMap.put("status", task.getStatus());
                    taskMap.put("createdAt", task.getCreatedAt());
                    taskList.add(taskMap);
                    System.out.println("任务 '" + taskTitle + "' 已添加到结果列表，创建时间: " + task.getCreatedAt());
                }
            }
            
            // 按创建时间倒序排序（最新的在前）
            taskList.sort((a, b) -> {
                String dateA = (String) a.get("createdAt");
                String dateB = (String) b.get("createdAt");
                if (dateA == null) return 1;
                if (dateB == null) return -1;
                return dateB.compareTo(dateA);
            });
            
            System.out.println("最终返回的任务列表数量: " + taskList.size());
            System.out.println("========== 获取项目任务列表结束 ==========");
            return Result.success(taskList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目任务列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取所有项目列表", description = "返回所有项目列表数据")
    @GetMapping("/all-projects")
    public Result getAllProjects(@RequestParam(required = false) String username) {
        try {
            System.out.println("========== 获取所有项目列表开始 ==========");
            System.out.println("请求用户名: " + username);
            
            // 获取所有项目
            System.out.println("1. 查询所有项目...");
            Iterable<Project> allProjects = projectService.findAll();
            List<Project> projectList = new ArrayList<>();
            allProjects.forEach(projectList::add);
            System.out.println("   数据库中总项目数量: " + projectList.size());
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            
            // 如果没有提供用户名，返回所有项目
            if (username == null || username.isEmpty()) {
                System.out.println("2. 没有提供用户名，返回所有项目");
                for (Project project : projectList) {
                    Map<String, Object> projectMap = buildProjectMap(project);
                    resultList.add(projectMap);
                }
                System.out.println("3. 返回结果: 项目数量=" + resultList.size());
                System.out.println("========== 获取所有项目列表结束 ==========");
                return Result.success(resultList);
            }
            
            // 获取用户ID（用户名字段）
            System.out.println("2. 解析用户ID...");
            Long userId = null;
            try {
                userId = Long.parseLong(username);
                System.out.println("   用户ID: " + userId);
            } catch (NumberFormatException e) {
                System.out.println("   用户名字段格式错误: " + username);
                System.out.println("3. 返回结果: 项目数量=0");
                System.out.println("========== 获取所有项目列表结束 ==========");
                return Result.success(resultList);
            }
            
            // 从项目成员表中查找用户参与的项目ID
            System.out.println("3. 查询项目成员表...");
            List<ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
            System.out.println("   用户 " + username + " 在项目成员表中的记录数量: " + projectMembers.size());
            
            if (projectMembers.isEmpty()) {
                System.out.println("   警告: 用户在项目成员表中没有记录");
                System.out.println("   原因可能：1) 用户未被添加到任何项目 2) 项目创建时未自动添加负责人");
            }
            
            List<Long> userProjectIds = new ArrayList<>();
            for (ProjectMember member : projectMembers) {
                if (member.getProjectId() != null) {
                    userProjectIds.add(member.getProjectId().longValue());
                    System.out.println("   找到项目ID: " + member.getProjectId() + ", 用户角色: " + member.getRoleType());
                }
            }
            
            // 遍历所有项目，只返回用户参与的项目
            System.out.println("4. 匹配用户有权限的项目...");
            for (Project project : projectList) {
                boolean hasAccess = userProjectIds.contains(project.getId());
                System.out.println("   项目: " + project.getName() + " (ID: " + project.getId() + "), 有权限: " + hasAccess);
                
                if (hasAccess) {
                    Map<String, Object> projectMap = buildProjectMap(project);
                    resultList.add(projectMap);
                }
            }
            
            System.out.println("5. 返回结果:");
            System.out.println("   返回的项目列表数量: " + resultList.size());
            System.out.println("========== 获取所有项目列表结束 ==========");
            return Result.success(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }
    
    // 辅助方法：构建项目Map
    private Map<String, Object> buildProjectMap(Project project) {
        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("id", project.getId());
        projectMap.put("title", project.getName());
        projectMap.put("name", project.getName());
        
        // 获取负责人姓名
        String managerName = "未知"; 
        if (project.getManagerId() != null) {
            try {
                var user = userService.findById(project.getManagerId().toString());
                if (user.isPresent()) {
                    managerName = user.get().getName();
                }
            } catch (Exception e) {
                System.out.println("获取负责人姓名失败: " + e.getMessage());
            }
        }
        projectMap.put("person", managerName);
        
        // 设置开始时间和预计完成时间
        projectMap.put("startTime", project.getStart_date());
        projectMap.put("finishTime", project.getEnd_date());
        
        // 设置进度
        projectMap.put("jinDu", project.getProgress() != null ? project.getProgress() : 0);
        
        // 根据状态设置显示文本
        String statusText = "已排期"; // 默认状态
        if (project.getStatus() != null) {
            switch (project.getStatus()) {
                case 0:
                    statusText = "未开始";
                    break;
                case 1:
                    statusText = "进行中";
                    break;
                case 2:
                    statusText = "已关闭";
                    break;
                case 3:
                    statusText = "已归档";
                    break;
            }
        }
        projectMap.put("states", statusText);
        
        return projectMap;
    }
    
    // 辅助方法：构建项目Map（包含工时、剩余任务、剩余Bug等详细信息）
    private Map<String, Object> buildProjectMapWithDetails(Project project) {
        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("title", project.getName());
        
        // 获取负责人姓名
        String managerName = "未知"; 
        if (project.getManagerId() != null) {
            try {
                var user = userService.findById(project.getManagerId().toString());
                if (user.isPresent()) {
                    managerName = user.get().getName();
                }
            } catch (Exception e) {
                System.out.println("获取负责人姓名失败: " + e.getMessage());
            }
        }
        projectMap.put("person", managerName);
        
        // 根据状态设置显示文本
        String statusText = "已排期"; // 默认状态
        if (project.getStatus() != null) {
            switch (project.getStatus()) {
                case 0:
                    statusText = "未开始";
                    break;
                case 1:
                    statusText = "进行中";
                    break;
            }
        }
        projectMap.put("states", statusText);
        
        // 从数据库获取实际数据（保持Long类型以正确匹配）
        Long projectId = project.getId();
        
        // 计算总工时（从tasks表中获取）
        int totalHours = 0;
        List<Task> tasks = taskService.findall();
        for (Task task : tasks) {
            if (task.getProjectId() != null && task.getProjectId().longValue() == projectId) {
                if (task.getEstimatedHours() != null) {
                    totalHours += task.getEstimatedHours().intValue();
                }
            }
        }
        projectMap.put("workTime", totalHours + "h");
        
        // 计算剩余任务数量（从tasks表中获取）
        int remainingTasks = 0;
        for (Task task : tasks) {
            if (task.getProjectId() != null && task.getProjectId().longValue() == projectId) {
                // 假设任务状态为0或1表示未完成
                if (task.getStatus() == null || task.getStatus() < 2) {
                    remainingTasks++;
                }
            }
        }
        projectMap.put("shengYuTask", remainingTasks);
        
        // 计算剩余Bug数量（从bugs表中获取）
        int remainingBugs = 0;
        List<Bug> bugs = bugService.findall();
        for (Bug bug : bugs) {
            if (bug.getProjectId() != null && bug.getProjectId().equals(projectId)) {
                // 假设Bug状态为0或1表示未解决
                if (bug.getStatus() == null || bug.getStatus() < 2) {
                    remainingBugs++;
                }
            }
        }
        projectMap.put("shengYuBug", remainingBugs);
        
        // 计算未完成测试套件数量（从test_suites表中获取）
        int remainingTestSuites = 0;
        List<com.example.springboot.entity.TestSuite> testSuites = testSuiteService.findAll();
        for (com.example.springboot.entity.TestSuite testSuite : testSuites) {
            if (testSuite.getProject_id() != null && testSuite.getProject_id().equals(project.getId())) {
                // 假设测试套件状态不为2表示未完成
                if (testSuite.getStatus() == null || testSuite.getStatus() != 2) {
                    remainingTestSuites++;
                }
            }
        }
        projectMap.put("remainingTestSuites", remainingTestSuites);
        
        // 设置完成时间和进度
        projectMap.put("finishTime", project.getEnd_date());
        projectMap.put("jinDu", project.getProgress() != null ? project.getProgress() : 0);
        
        // 判断项目是否未完成：有未完成任务、未解决Bug或未完成测试套件
        boolean isIncomplete = remainingTasks > 0 || remainingBugs > 0 || remainingTestSuites > 0;
        projectMap.put("isIncomplete", isIncomplete);
        
        return projectMap;
    }

    @Operation(summary = "删除项目", description = "根据ID删除项目")
    @DeleteMapping("/projects/{id}")
    public Result deleteProject(@PathVariable Long id, @RequestParam String username) {
        try {
            // 检查用户角色，只有管理员和产品经理可以删除项目
            boolean isProductManager = rolePermissionUtils.isProductManager(username);
            boolean isAdmin = rolePermissionUtils.isAdmin(username);
            
            if (!isProductManager && !isAdmin) {
                return Result.error("权限不足，只有管理员和产品经理可以删除项目");
            }
            
            projectService.deleteById(id);
            return Result.success("删除项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除项目失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "创建任务", description = "创建新任务")
    @PostMapping("/tasks")
    public Result createTask(@RequestBody Task task) {
        try {
            System.out.println("创建任务请求: " + task);
            Task savedTask = taskService.save(task);
            System.out.println("创建任务成功: " + savedTask);
            return Result.success("创建任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "更新任务", description = "更新任务信息")
    @PutMapping("/tasks/{id}")
    public Result updateTask(@PathVariable Integer id, @RequestBody Task task) {
        try {
            System.out.println("更新任务请求, ID: " + id + ", 数据: " + task);
            // 先查找任务是否存在
            List<Task> tasks = taskService.findall();
            Task existingTask = null;
            for (Task t : tasks) {
                if (t.getId().equals(id)) {
                    existingTask = t;
                    break;
                }
            }
            
            if (existingTask == null) {
                return Result.error("任务不存在");
            }
            
            // 更新任务信息
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setProjectId(task.getProjectId());
            existingTask.setAssigneeId(task.getAssigneeId());
            existingTask.setStatus(task.getStatus());
            existingTask.setPriority(task.getPriority());
            existingTask.setStartDate(task.getStartDate());
            existingTask.setDueDate(task.getDueDate());
            
            Task updatedTask = taskService.save(existingTask);
            System.out.println("更新任务成功: " + updatedTask);
            return Result.success("更新任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新任务失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除任务", description = "根据ID删除任务")
    @DeleteMapping("/tasks/{id}")
    public Result deleteTask(@PathVariable Integer id) {
        try {
            System.out.println("删除任务请求, ID: " + id);
            // 先查找任务是否存在
            List<Task> tasks = taskService.findall();
            Task existingTask = null;
            for (Task t : tasks) {
                if (t.getId().equals(id)) {
                    existingTask = t;
                    break;
                }
            }
            
            if (existingTask == null) {
                return Result.error("任务不存在");
            }
            
            // 删除任务
            taskService.deleteById(id);
            System.out.println("删除任务成功: " + id);
            return Result.success("删除任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除任务失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "更新任务状态", description = "更新任务状态")
    @PutMapping("/tasks/{id}/status")
    public Result updateTaskStatus(@PathVariable Integer id, @RequestParam Integer status) {
        try {
            System.out.println("更新任务状态请求, ID: " + id + ", 状态: " + status);
            // 先查找任务是否存在
            List<Task> tasks = taskService.findall();
            Task existingTask = null;
            for (Task t : tasks) {
                if (t.getId().equals(id)) {
                    existingTask = t;
                    break;
                }
            }
            
            if (existingTask == null) {
                return Result.error("任务不存在");
            }
            
            // 更新任务状态
            existingTask.setStatus(status);
            Task updatedTask = taskService.save(existingTask);
            System.out.println("更新任务状态成功: " + updatedTask);
            return Result.success("更新任务状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新任务状态失败: " + e.getMessage());
        }
    }

    @Operation(summary = "提交代码", description = "提交代码并更新任务进度")
    @PostMapping("/task/submit-code")
    public Result submitCode(@RequestBody Map<String, Object> request) {
        try {
            Integer taskId = (Integer) request.get("taskId");
            String repositoryUrl = (String) request.get("repositoryUrl");
            String branch = (String) request.get("branch");
            String commitMessage = (String) request.get("commitMessage");
            Integer progress = (Integer) request.get("progress");
            Integer newProgress = (Integer) request.get("newProgress");
            
            System.out.println("提交代码请求, 任务ID: " + taskId + ", 进度: " + progress + ", 新进度: " + newProgress);
            
            // 查找任务是否存在
            List<Task> tasks = taskService.findall();
            Task existingTask = null;
            for (Task t : tasks) {
                if (t.getId().equals(taskId)) {
                    existingTask = t;
                    break;
                }
            }
            
            if (existingTask == null) {
                return Result.error("任务不存在");
            }
            
            // 更新任务进度
            existingTask.setProgress(newProgress);
            
            // 如果进度达到100%，设置任务状态为已完成
            if (newProgress != null && newProgress >= 100) {
                existingTask.setStatus(3); // 3表示已完成
            }
            
            Task updatedTask = taskService.save(existingTask);
            System.out.println("代码提交成功，任务进度更新为: " + updatedTask.getProgress());
            
            return Result.success("代码提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("代码提交失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目详情", description = "根据ID获取项目详情")
    @GetMapping("/projects/{id}")
    public Result getProjectById(@PathVariable Long id) {
        try {
            Optional<Project> projectOpt = projectService.findById(id);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("id", project.getId());
                projectMap.put("title", project.getName());
                projectMap.put("product_id", project.getProduct_id());
                projectMap.put("startTime", project.getStart_date());
                projectMap.put("finishTime", project.getEnd_date());
                
                // 获取负责人姓名
                String managerName = "未知"; 
                if (project.getManagerId() != null) {
                    try {
                        var user = userService.findById(project.getManagerId().toString());
                        if (user.isPresent()) {
                            managerName = user.get().getName();
                        }
                    } catch (Exception e) {
                        System.out.println("获取负责人姓名失败: " + e.getMessage());
                    }
                }
                projectMap.put("person", managerName);
                
                projectMap.put("accessControl", project.getAccess_control());
                projectMap.put("team_id", project.getTeam_id());
                projectMap.put("description", project.getDescription());
                projectMap.put("progress", project.getProgress());
                projectMap.put("status", project.getStatus());
                projectMap.put("manager_id", project.getManagerId());
                
                return Result.success(projectMap);
            } else {
                return Result.error("项目不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取产品列表", description = "返回与当前登录用户有关的产品列表")
    @GetMapping("/products")
    public Result getProducts(@RequestParam String username) {
        try {
            System.out.println("获取产品列表请求，用户名: " + username);
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
            try {
                Long userId = Long.parseLong(username);
                List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                System.out.println("用户 " + username + " 参与的项目成员记录数: " + projectMembers.size());
                for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                    if (member.getProjectId() != null) {
                        userProjectIds.add(member.getProjectId());
                        System.out.println("用户参与的项目ID: " + member.getProjectId());
                    }
                }
                System.out.println("用户参与的项目ID列表: " + userProjectIds);
            } catch (NumberFormatException e) {
                System.out.println("用户名不是数字格式: " + username);
                return Result.success(new ArrayList<>());
            }
            
            // 获取用户参与项目的产品ID列表
            Set<Long> userProductIds = new HashSet<>();
            for (Long projectId : userProjectIds) {
                Optional<Project> projectOpt = projectService.findById(projectId);
                if (projectOpt.isPresent()) {
                    Project project = projectOpt.get();
                    if (project.getProduct_id() != null) {
                        userProductIds.add(project.getProduct_id());
                        System.out.println("项目ID " + projectId + " 对应的产品ID: " + project.getProduct_id());
                    }
                }
            }
            System.out.println("用户参与项目的产品ID列表: " + userProductIds);
            
            // 获取所有项目
            List<Project> allProjects = (List<Project>) projectService.findAll();
            
            // 获取所有Bug
            List<Bug> allBugs = bugService.findall();
            
            // 获取所有产品
            List<Map<String, Object>> productList = new ArrayList<>();
            Iterable<Product> products = productService.findAll();
            for (Product product : products) {
                // 只返回用户参与项目的产品
                if (userProductIds.contains(product.getId())) {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    
                    // 计算产品的项目数量（只计算用户参与的项目）
                    int projectCount = 0;
                    int iterationCount = 0;
                    int bugCount = 0;
                    
                    // 收集该产品下用户参与的所有项目ID
                    Set<Long> productProjectIds = new HashSet<>();
                    
                    for (Project project : allProjects) {
                        if (project.getProduct_id() != null && project.getProduct_id().equals(product.getId()) && userProjectIds.contains(project.getId())) {
                            projectCount++;
                            productProjectIds.add(project.getId());
                            // 假设项目的迭代数量可以通过项目的某些属性计算，这里暂时设置为0
                            // 实际应该根据迭代表的数据来计算
                            iterationCount += 0;
                        }
                    }
                    
                    // 计算产品下所有项目的Bug总数
                    for (Bug bug : allBugs) {
                        if (bug.getProjectId() != null) {
                            long bugProjectId = bug.getProjectId().longValue();
                            if (productProjectIds.contains(bugProjectId)) {
                                bugCount++;
                            }
                        }
                    }
                    
                    productMap.put("projectCount", projectCount);
                    productMap.put("iterationCount", iterationCount);
                    productMap.put("activeBugs", bugCount); // Bug数量
                    productList.add(productMap);
                    System.out.println("添加产品到列表: " + product.getName() + "，项目数量: " + projectCount + "，迭代数量: " + iterationCount + "，Bug数量: " + bugCount);
                }
            }
            
            System.out.println("返回的产品列表数量: " + productList.size());
            return Result.success(productList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目成员列表", description = "根据项目ID获取项目成员列表")
    @GetMapping("/projects/{id}/members")
    public Result getProjectMembers(@PathVariable Long id) {
        try {
            List<Map<String, Object>> memberList = new ArrayList<>();
            List<ProjectMember> projectMembers = projectMemberService.findByProjectId(id);
            for (ProjectMember member : projectMembers) {
                if (member.getUserId() != null) {
                    try {
                        var user = userService.findById(member.getUserId().toString());
                        if (user.isPresent()) {
                            Map<String, Object> memberMap = new HashMap<>();
                            memberMap.put("id", user.get().getUsername());
                            memberMap.put("name", user.get().getName());
                            
                            // 根据用户ID查询团队成员记录
                            List<TeamMember> teamMembers = teamMemberService.findByUserId(member.getUserId());
                            if (!teamMembers.isEmpty()) {
                                // 获取第一个团队成员的角色
                                TeamMember teamMember = teamMembers.get(0);
                                memberMap.put("role", teamMember.getRoleInTeam());
                            } else {
                                memberMap.put("role", member.getRoleType());
                            }
                            
                            memberList.add(memberMap);
                        }
                    } catch (Exception e) {
                        System.out.println("获取项目成员失败: " + e.getMessage());
                    }
                }
            }
            return Result.success(memberList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目成员列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增项目", description = "新增项目")
    @PostMapping("/projects")
    public Result addProject(@RequestBody Map<String, Object> projectData) {
        try {
            Project project = new Project();
            project.setName((String) projectData.get("name"));
            
            // 处理manager_id
            if (projectData.get("manager_id") != null) {
                try {
                    project.setManagerId(Long.parseLong(projectData.get("manager_id").toString()));
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
            
            // 处理team_id
            if (projectData.get("team_id") != null) {
                try {
                    project.setTeam_id(Long.parseLong(projectData.get("team_id").toString()));
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
            
            // 处理product_id
            if (projectData.get("product_id") != null) {
                try {
                    project.setProduct_id(Long.parseLong(projectData.get("product_id").toString()));
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
            
            // 处理日期
            if (projectData.get("start_date") != null) {
                try {
                    project.setStart_date(new SimpleDateFormat("yyyy-MM-dd").parse((String) projectData.get("start_date")));
                } catch (ParseException e) {
                    // 忽略转换错误
                }
            }
            
            if (projectData.get("end_date") != null) {
                try {
                    project.setEnd_date(new SimpleDateFormat("yyyy-MM-dd").parse((String) projectData.get("end_date")));
                } catch (ParseException e) {
                    // 忽略转换错误
                }
            }
            
            // 处理状态和进度
            if (projectData.get("status") != null) {
                try {
                    project.setStatus(Integer.parseInt(projectData.get("status").toString()));
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
            
            if (projectData.get("progress") != null) {
                try {
                    project.setProgress(Integer.parseInt(projectData.get("progress").toString()));
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
            
            // 处理描述
            if (projectData.get("description") != null) {
                project.setDescription((String) projectData.get("description"));
            }
            
            // 保存项目
            Project savedProject = projectService.save(project);
            
            System.out.println("项目已保存，项目ID: " + savedProject.getId());
            
            // 如果选择了团队，将团队成员都添加到项目成员表中
            if (savedProject.getTeam_id() != null) {
                Integer teamId = savedProject.getTeam_id().intValue();
                List<TeamMember> teamMembers = teamMemberService.findByTeamId(teamId);
                
                for (TeamMember teamMember : teamMembers) {
                    // 如果是负责人，跳过（后面单独处理，使用用户角色ID）
                    if (savedProject.getManagerId() != null && teamMember.getUserId().equals(savedProject.getManagerId())) {
                        continue;
                    }
                    
                    ProjectMember member = new ProjectMember();
                    member.setProjectId(savedProject.getId());
                    member.setUserId(teamMember.getUserId());
                    member.setRoleType(teamMember.getRoleInTeam());
                    member.setTeamId(teamId);
                    projectMemberService.save(member);
                }
            }
            
            // 添加负责人到项目成员表，使用用户的角色ID
            if (savedProject.getManagerId() != null) {
                ProjectMember managerMember = new ProjectMember();
                managerMember.setProjectId(savedProject.getId());
                managerMember.setUserId(savedProject.getManagerId());
                
                var managerUser = userService.findById(savedProject.getManagerId().toString());
                if (managerUser.isPresent()) {
                    Long roleId = managerUser.get().getRole_id();
                    managerMember.setRoleType(roleId != null ? roleId.toString() : "2");
                } else {
                    managerMember.setRoleType("2");
                }
                
                if (savedProject.getTeam_id() != null) {
                    managerMember.setTeamId(savedProject.getTeam_id().intValue());
                }
                
                projectMemberService.save(managerMember);
            }
            
            return Result.success("新增项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增项目失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新项目", description = "更新项目信息")
    @PutMapping("/projects/{id}")
    public Result updateProject(@PathVariable Long id, @RequestBody Map<String, Object> projectData) {
        try {
            Optional<Project> projectOpt = projectService.findById(id);
            if (!projectOpt.isPresent()) {
                return Result.error("项目不存在");
            }
            
            Project project = projectOpt.get();
            
            if (projectData.containsKey("name")) {
                project.setName((String) projectData.get("name"));
            }
            if (projectData.containsKey("product_id")) {
                Object productIdObj = projectData.get("product_id");
                if (productIdObj instanceof Integer) {
                    project.setProduct_id(((Integer) productIdObj).longValue());
                } else if (productIdObj instanceof Long) {
                    project.setProduct_id((Long) productIdObj);
                }
            }
            if (projectData.containsKey("start_date")) {
                Object startDateObj = projectData.get("start_date");
                if (startDateObj instanceof String) {
                    project.setStart_date(java.sql.Date.valueOf((String) startDateObj));
                } else if (startDateObj instanceof java.util.Date) {
                    project.setStart_date(new java.sql.Date(((java.util.Date) startDateObj).getTime()));
                }
            }
            if (projectData.containsKey("end_date")) {
                Object endDateObj = projectData.get("end_date");
                if (endDateObj instanceof String) {
                    project.setEnd_date(java.sql.Date.valueOf((String) endDateObj));
                } else if (endDateObj instanceof java.util.Date) {
                    project.setEnd_date(new java.sql.Date(((java.util.Date) endDateObj).getTime()));
                }
            }
            if (projectData.containsKey("manager_id")) {
                Object managerIdObj = projectData.get("manager_id");
                if (managerIdObj instanceof Integer) {
                    project.setManagerId(((Integer) managerIdObj).longValue());
                } else if (managerIdObj instanceof Long) {
                    project.setManagerId((Long) managerIdObj);
                }
            }
            if (projectData.containsKey("team_id")) {
                Object teamIdObj = projectData.get("team_id");
                if (teamIdObj instanceof Integer) {
                    project.setTeam_id(((Integer) teamIdObj).longValue());
                } else if (teamIdObj instanceof Long) {
                    project.setTeam_id((Long) teamIdObj);
                }
            }
            if (projectData.containsKey("description")) {
                project.setDescription((String) projectData.get("description"));
            }
            if (projectData.containsKey("access_control")) {
                Object accessControlObj = projectData.get("access_control");
                if (accessControlObj instanceof String) {
                    String accessControlStr = (String) accessControlObj;
                    project.setAccess_control("private".equals(accessControlStr) ? 1 : 0);
                } else if (accessControlObj instanceof Integer) {
                    project.setAccess_control((Integer) accessControlObj);
                }
            }
            if (projectData.containsKey("progress")) {
                Object progressObj = projectData.get("progress");
                if (progressObj instanceof Integer) {
                    project.setProgress((Integer) progressObj);
                } else if (progressObj instanceof String) {
                    project.setProgress(Integer.parseInt((String) progressObj));
                }
            }
            if (projectData.containsKey("status")) {
                Object statusObj = projectData.get("status");
                if (statusObj instanceof Integer) {
                    project.setStatus((Integer) statusObj);
                } else if (statusObj instanceof String) {
                    project.setStatus(Integer.parseInt((String) statusObj));
                }
            }
            
            projectService.save(project);
            return Result.success("更新项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新项目失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新项目状态", description = "更新项目状态")
    @PutMapping("/projects/{id}/status")
    public Result updateProjectStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            Optional<Project> projectOpt = projectService.findById(id);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                project.setStatus(status);
                projectService.save(project);
                return Result.success("更新项目状态成功");
            } else {
                return Result.error("项目不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新项目状态失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取工作台统计数据", description = "返回工作台统计数据")
    @GetMapping("/statistics")
    public Result getStatistics(@RequestParam String username) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 1. 获取用户信息，判断用户角色
            boolean isProductManager = false;
            var user = userService.findByUsername(username);
            
            if (user != null) {
                // 角色ID定义：1=超级管理员，2=产品经理，3=开发者，4=测试者
                // 超级管理员和产品经理都应该能看到所有Bug
                if (user.getRole_id() != null && (user.getRole_id() == 1 || user.getRole_id() == 2)) {
                    isProductManager = true;
                }
            } else {
                return Result.error("用户不存在");
            }
            
            // 2. 获取待审批数
            int approveState = 0;
            List<ProjectApproval> approvals = projectApprovalService.findall();
            
            // 获取用户参与的项目ID列表
            Set<Long> userProjectIds = new HashSet<>();
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
            
            for (ProjectApproval approval : approvals) {
                // 由于ProjectApproval实体类没有status字段，这里简化处理，直接统计所有审批
                // 如果是产品经理，显示所有审批
                if (isProductManager) {
                    approveState++;
                } else {
                    // 否则只显示用户参与项目的审批
                    if (approval.getProject_id() != null) {
                        Long projectId = approval.getProject_id().longValue();
                        if (userProjectIds.contains(projectId)) {
                            approveState++;
                        }
                    }
                }
            }
            
            // 3. 获取任务数（只统计当前用户是负责人的任务）
            int taskState = 0;
            List<Task> tasks = taskService.findall();
            
            // 通过username解析为Long，因为username存储的是学号/工号
            Long taskAssigneeId = null;
            try {
                taskAssigneeId = Long.parseLong(username);
            } catch (NumberFormatException e) {
                // 用户名不是数字格式，忽略
            }
            
            if (taskAssigneeId != null) {
                for (Task task : tasks) {
                    // 只统计当前用户是负责人的任务（Task.assigneeId是Integer，需要转换）
                    if (task.getAssigneeId() != null && task.getAssigneeId().longValue() == taskAssigneeId) {
                        taskState++;
                    }
                }
            }
            
            // 4. 获取Bug数（只统计当前用户是负责人的Bug）
            int bugState = 0;
            List<Bug> bugs = bugService.findall();
            System.out.println("获取到的Bug数量: " + bugs.size());
            System.out.println("当前用户名: " + username);
            System.out.println("当前用户ID: " + taskAssigneeId);
            
            // 检查数据库连接和Bug数据
            if (bugs == null) {
                System.out.println("Bug列表为null");
            } else {
                System.out.println("Bug列表不为null，大小: " + bugs.size());
                for (Bug bug : bugs) {
                    System.out.println("Bug详情: ID=" + bug.getId() + ", 标题=" + bug.getTitle() + ", 负责人ID=" + bug.getAssigneeId() + ", 状态=" + bug.getStatus());
                }
            }
            
            if (taskAssigneeId != null) {
                for (Bug bug : bugs) {
                    // 只统计当前用户是负责人的Bug（Bug.assigneeId是Long）
                    if (bug.getAssigneeId() != null && bug.getAssigneeId().equals(taskAssigneeId)) {
                        bugState++;
                        System.out.println("匹配到Bug: " + bug.getId());
                    }
                }
            }
            System.out.println("最终Bug数: " + bugState);
            
            // 6. 获取文档数（暂时模拟）
            int passageState = 0;
            
            // 7. 昨天处理任务和Bug的次数（暂时模拟）
            int bug = 0;
            
            // 构建返回数据
            statistics.put("bug", bug);
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
    
    @Operation(summary = "更新审批状态", description = "更新审批状态")
    @PutMapping("/approvals/{id}")
    public Result updateApprovalStatus(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            System.out.println("更新审批状态，审批ID: " + id + ", 请求体: " + body);
            
            // 根据ID查找审批
            ProjectApproval approval = null;
            List<ProjectApproval> approvals = projectApprovalService.findall();
            for (ProjectApproval a : approvals) {
                if (a.getId() != null && a.getId().equals(id)) {
                    approval = a;
                    break;
                }
            }
            
            if (approval != null) {
                System.out.println("找到审批记录: " + approval.getId() + ", 类型: " + approval.getType());
                
                // 更新审批状态
                String action = (String) body.get("action");
                System.out.println("审批操作: " + action);
                
                if (action != null) {
                    approval.setAction(action);
                    
                    // 当操作是通过或拒绝时，更新finish_time
                    if ("通过".equals(action) || "退回".equals(action)) {
                        String approvalTime = (String) body.get("approvalTime");
                        if (approvalTime != null) {
                            approval.setFinish_time(approvalTime);
                        }
                    }
                }
                
                // 保存更新后的审批状态
                projectApprovalService.save(approval);
                System.out.println("审批状态已保存");
                
                // 如果审批记录关联了反馈，同步更新反馈状态
                if (approval.getFeedback_id() != null) {
                    System.out.println("审批关联反馈ID: " + approval.getFeedback_id());
                    com.example.springboot.entity.Feedback feedback = feedbackService.findEntityById(approval.getFeedback_id());
                    if (feedback != null) {
                        System.out.println("找到反馈: " + feedback.getTitle() + ", 当前状态: " + feedback.getStatus());
                        
                        if ("通过".equals(action)) {
                            feedback.setStatus("处理中");
                            System.out.println("更新反馈状态为: 处理中");
                        } else if ("退回".equals(action)) {
                            feedback.setStatus("待处理");
                            System.out.println("更新反馈状态为: 待处理");
                        }
                        feedback.setUpdatedAt(new Date());
                        feedbackService.save(feedback);
                        System.out.println("反馈状态已同步更新");
                    } else {
                        System.out.println("未找到关联的反馈记录");
                    }
                } else {
                    System.out.println("审批记录没有关联的反馈ID");
                }
                
                return Result.success("审批状态更新成功");
            } else {
                System.out.println("审批不存在");
                return Result.error("审批不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新审批状态失败: " + e.getMessage());
        }
    }
    
    @Resource
    private OperationLogService operationLogService;
    
    @Operation(summary = "记录操作日志", description = "记录操作日志")
    @PostMapping("/operation-logs")
    public Result recordOperationLog(@RequestBody Map<String, Object> body) {
        try {
            // 创建操作日志对象
            OperationLog operationLog = new OperationLog();
            
            // 设置用户ID
            Object usernameObj = body.get("username");
            if (usernameObj != null) {
                String username = usernameObj.toString();
                operationLog.setUser_id(username);
            }
            
            // 设置操作内容
            String action = (String) body.get("action");
            if (action != null) {
                operationLog.setAction(action);
            }
            
            // 设置目标ID
            Object targetIdObj = body.get("targetId");
            if (targetIdObj != null) {
                try {
                    if (targetIdObj instanceof Integer) {
                        operationLog.setTarget_id(((Integer) targetIdObj).longValue());
                    } else if (targetIdObj instanceof Long) {
                        operationLog.setTarget_id((Long) targetIdObj);
                    } else if (targetIdObj instanceof String) {
                        operationLog.setTarget_id(Long.parseLong((String) targetIdObj));
                    }
                } catch (NumberFormatException e) {
                    // 目标ID不是数字格式，跳过
                }
            }
            
            // 设置模块
            String targetType = (String) body.get("targetType");
            if (targetType != null) {
                operationLog.setModule(targetType);
            }
            
            // 设置创建时间
            String createTimeStr = (String) body.get("createTime");
            if (createTimeStr != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date createTime = sdf.parse(createTimeStr);
                    operationLog.setCreated_at(createTime);
                } catch (ParseException e) {
                    // 时间格式错误，使用当前时间
                    operationLog.setCreated_at(new Date());
                }
            } else {
                // 没有提供时间，使用当前时间
                operationLog.setCreated_at(new Date());
            }
            
            // 保存操作日志到数据库
            operationLogService.save(operationLog);
            System.out.println("记录操作日志: " + body);
            return Result.success("操作日志记录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("记录操作日志失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "更新Bug状态", description = "更新Bug状态和修复说明")
    @PutMapping("/bugs/{id}/status")
    public Result updateBugStatus(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            System.out.println("更新Bug状态请求, ID: " + id + ", body: " + body);
            
            // 先查找Bug是否存在
            List<Bug> bugs = bugService.findall();
            Bug existingBug = null;
            for (Bug b : bugs) {
                if (b.getId().equals(id)) {
                    existingBug = b;
                    break;
                }
            }
            
            if (existingBug == null) {
                return Result.error("Bug不存在");
            }
            
            // 更新状态
            if (body.containsKey("status")) {
                Object statusObj = body.get("status");
                if (statusObj instanceof Integer) {
                    existingBug.setStatus((Integer) statusObj);
                }
            }
            
            // 更新修复说明
            if (body.containsKey("solution")) {
                existingBug.setSolution((String) body.get("solution"));
            }
            
            // 保存更新
            Bug updatedBug = bugService.save(existingBug);
            System.out.println("更新Bug状态成功: " + updatedBug);
            
            return Result.success("Bug状态更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新Bug状态失败: " + e.getMessage());
        }
    }
}