package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.ProjectApproval;
import com.example.springboot.entity.ProjectMember;
import com.example.springboot.entity.Product;
import com.example.springboot.entity.TeamMember;

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
                
                approvalList.add(approvalMap);
            }
            
            System.out.println("返回的审批列表数量: " + approvalList.size());
            return Result.success(approvalList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取审批列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取任务列表", description = "返回任务列表数据")
    @GetMapping("/tasks")
    public Result getTasks(@RequestParam(required = false) String username) {
        try {
            List<Task> tasks = taskService.findall();
            List<Map<String, Object>> taskList = new ArrayList<>();
            
            // 获取用户参与的项目ID列表（如果是产品经理）
            List<Long> userProjectIds = new ArrayList<>();
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
                                System.out.println("用户角色id为" + user.getRole_id() + "，返回所有任务");
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
                // 如果指定了用户名
                if (username != null && !username.isEmpty()) {
                    try {
                        Integer userId = Integer.parseInt(username);
                        
                        // 如果是产品经理
                        if (isProductManager) {
                            // 产品经理可以看到所有任务，包括projectId为null的任务
                            // 不需要过滤，直接显示所有任务
                            System.out.println("产品经理看到任务: " + task.getTitle());
                        } else {
                            // 非产品经理，只返回被指派给当前用户的任务
                            if (task.getAssigneeId() == null || !task.getAssigneeId().equals(userId)) {
                                continue; // 跳过不是当前用户的任务
                            }
                        }
                    } catch (NumberFormatException e) {
                        // 用户名不是数字格式，如果是产品经理，仍然显示所有任务
                        if (!isProductManager) {
                            continue; // 非产品经理，跳过
                        }
                    }
                }
                
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("id", task.getId());
                taskMap.put("title", task.getTitle());
                taskMap.put("description", task.getDescription());
                taskMap.put("project_id", task.getProjectId());
                taskMap.put("requirement_id", task.getRequirementId());
                taskMap.put("parent_id", task.getParentId());
                taskMap.put("creator_id", task.getCreatorId());
                taskMap.put("assignee_id", task.getAssigneeId());
                taskMap.put("assignee_name", "未指派"); // 默认值
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
                
                taskList.add(taskMap);
                System.out.println("添加任务到列表: " + task.getTitle());
            }
            
            System.out.println("返回的任务列表数量: " + taskList.size());
            return Result.success(taskList);
        } catch (Exception e) {
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
            List<Bug> bugs = bugService.findall();
            // 如果指定了用户名，只返回当前用户的Bug
            if (username != null && !username.isEmpty()) {
                try {
                    Integer userId = Integer.parseInt(username);
                    bugs = bugs.stream()
                            .filter(bug -> bug.getReporterId() != null && bug.getReporterId().equals(userId))
                            .toList();
                } catch (NumberFormatException e) {
                    // 用户名不是数字格式，返回空列表
                    bugs = new ArrayList<>();
                }
            }
            return Result.success(bugs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取Bug列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目列表", description = "返回项目列表数据")
    @GetMapping("/projects")
    public Result getProjects(@RequestParam String username) {
        try {
            System.out.println("获取项目列表请求，用户名: " + username);
            
            // 获取用户角色
            boolean isProductManager = rolePermissionUtils.isProductManager(username);
            boolean isDeveloper = rolePermissionUtils.isDeveloper(username);
            boolean isTester = rolePermissionUtils.isTester(username);
            boolean isAdmin = rolePermissionUtils.isAdmin(username);
            
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            if (isAdmin || isProductManager) {
                // 管理员和产品经理可以看到所有项目
                Iterable<Project> allProjects = projectService.findAll();
                for (Project project : allProjects) {
                    Map<String, Object> projectMap = new HashMap<>();
                    projectMap.put("id", project.getId());
                    projectMap.put("projectName", project.getName());
                    
                    // 从数据库查询项目成员数
                    List<com.example.springboot.entity.ProjectMember> projectMemberList = projectMemberService.findByProjectId(project.getId());
                    projectMap.put("projectMember", projectMemberList.size());
                    
                    projectMap.put("finishTime", project.getEnd_date());
                    projectMap.put("degree", project.getProgress() != null ? project.getProgress() : 0);
                    projectMap.put("status", project.getStatus() != null ? project.getStatus() : 0);
                    projectList.add(projectMap);
                }
            } else if (isDeveloper || isTester) {
                // 开发者和测试者只能看到自己参与的项目
                var user = userService.findById(username);
                if (user.isPresent()) {
                    Long userId = Long.parseLong(user.get().getUsername());
                    System.out.println("用户ID: " + userId);
                    
                    // 根据用户ID查询参与的项目成员记录
                    List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                    System.out.println("获取到的项目成员记录数量: " + projectMembers.size());
                    
                    // 遍历项目成员记录，获取对应的项目信息
                    for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                        Long projectId = member.getProjectId();
                        System.out.println("项目ID: " + projectId);
                        if (projectId != null) {
                            var project = projectService.findById(projectId);
                            System.out.println("获取到的项目信息: " + (project.isPresent() ? project.get().getName() : "不存在"));
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
                                System.out.println("项目进度: " + (p.getProgress() != null ? p.getProgress() : 0));
                                projectList.add(projectMap);
                            }
                        }
                    }
                }
            }
            
            System.out.println("返回的项目列表数量: " + projectList.size());
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
            // 获取用户角色
            boolean isProductManager = false;
            boolean isDeveloper = false;
            boolean isTester = false;
            boolean isAdmin = false;
            
            if (username != null) {
                isProductManager = rolePermissionUtils.isProductManager(username);
                isDeveloper = rolePermissionUtils.isDeveloper(username);
                isTester = rolePermissionUtils.isTester(username);
                isAdmin = rolePermissionUtils.isAdmin(username);
            }
            
            // 获取所有项目
            Iterable<Project> allProjects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : allProjects) {
                // 过滤出未完成的项目（状态不是2已关闭或3已归档）
                if (project.getStatus() == null || (project.getStatus() != 2 && project.getStatus() != 3)) {
                    // 根据用户角色过滤项目
                    boolean hasAccess = false;
                    
                    if (isAdmin || isProductManager) {
                        // 管理员和产品经理可以看到所有项目
                        hasAccess = true;
                    } else if (isDeveloper || isTester) {
                        // 开发者和测试者只能看到自己参与的项目
                        try {
                            // 使用RolePermissionUtils检查用户是否有权限访问该项目
                            hasAccess = rolePermissionUtils.hasProjectAccess(username, project.getId());
                        } catch (Exception e) {
                            // 权限检查失败，跳过
                        }
                    }
                    
                    if (hasAccess || username == null) {
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
                        
                        // 从数据库获取实际数据
                        Integer projectId = project.getId().intValue();
                        
                        // 计算总工时（从tasks表中获取）
                        int totalHours = 0;
                        List<Task> tasks = taskService.findall();
                        for (Task task : tasks) {
                            if (task.getProjectId() != null && task.getProjectId().equals(projectId)) {
                                if (task.getEstimatedHours() != null) {
                                    totalHours += task.getEstimatedHours().intValue();
                                }
                            }
                        }
                        projectMap.put("workTime", totalHours + "h");
                        

                        
                        // 计算剩余任务数量（从tasks表中获取）
                        int remainingTasks = 0;
                        for (Task task : tasks) {
                            if (task.getProjectId() != null && task.getProjectId().equals(projectId)) {
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
                        
                        // 设置完成时间和进度
                        projectMap.put("finishTime", project.getEnd_date());
                        projectMap.put("jinDu", project.getProgress() != null ? project.getProgress() : 0);
                        
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

    @Operation(summary = "获取所有项目列表", description = "返回所有项目列表数据")
    @GetMapping("/all-projects")
    public Result getAllProjects(@RequestParam(required = false) String username) {
        try {
            // 获取所有项目
            Iterable<Project> allProjects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : allProjects) {
                // 检查用户是否有权限访问该项目
                boolean hasAccess = true;
                if (username != null) {
                    hasAccess = rolePermissionUtils.hasProjectAccess(username, project.getId());
                }
                
                if (hasAccess) {
                    Map<String, Object> projectMap = new HashMap<>();
                    projectMap.put("id", project.getId());
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
                    
                    projectList.add(projectMap);
                }
            }
            
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
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
                    for (Project project : allProjects) {
                        if (project.getProduct_id() != null && project.getProduct_id().equals(product.getId()) && userProjectIds.contains(project.getId())) {
                            projectCount++;
                            // 假设项目的迭代数量可以通过项目的某些属性计算，这里暂时设置为0
                            // 实际应该根据迭代表的数据来计算
                            iterationCount += 0;
                        }
                    }
                    
                    productMap.put("projectCount", projectCount);
                    productMap.put("iterationCount", iterationCount);
                    productList.add(productMap);
                    System.out.println("添加产品到列表: " + product.getName() + "，项目数量: " + projectCount + "，迭代数量: " + iterationCount);
                }
            }
            
            System.out.println("返回的产品列表数量: " + productList.size());
            return Result.success(productList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取产品列表失败: " + e.getMessage());
        }
    }

    @Resource
    TeamMemberService teamMemberService;

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
            
            projectService.save(project);
            return Result.success("新增项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增项目失败: " + e.getMessage());
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
            var userOptional = userService.findById(username);
            
            if (userOptional.isPresent()) {
                var user = userOptional.get();
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
            
            // 3. 获取任务数
            int taskState = 0;
            List<Task> tasks = taskService.findall();
            
            for (Task task : tasks) {
                // 如果是产品经理，显示所有任务
                if (isProductManager) {
                    taskState++;
                } else {
                    // 否则只显示与用户参与的项目相关的任务
                    if (task.getProjectId() != null) {
                        Long projectId = task.getProjectId().longValue();
                        if (userProjectIds.contains(projectId)) {
                            taskState++;
                        }
                    }
                }
            }
            
            // 4. 获取Bug数
            int bugState = 0;
            List<Bug> bugs = bugService.findall();
            System.out.println("获取到的Bug数量: " + bugs.size());
            System.out.println("当前用户名: " + username);
            System.out.println("是否为产品经理: " + isProductManager);
            
            // 检查数据库连接和Bug数据
            if (bugs == null) {
                System.out.println("Bug列表为null");
            } else {
                System.out.println("Bug列表不为null，大小: " + bugs.size());
                for (Bug bug : bugs) {
                    System.out.println("Bug详情: ID=" + bug.getId() + ", 标题=" + bug.getTitle() + ", 负责人ID=" + bug.getAssigneeId() + ", 状态=" + bug.getStatus());
                }
            }
            
            for (Bug bug : bugs) {
                // 如果是产品经理，显示所有Bug
                if (isProductManager) {
                    bugState++;
                    System.out.println("产品经理统计Bug: " + bug.getId());
                } else {
                    // 否则只显示当前用户的Bug
                    try {
                        Integer userId = Integer.parseInt(username);
                        System.out.println("Bug ID: " + bug.getId() + ", Assignee ID: " + bug.getAssigneeId() + ", 用户ID: " + userId);
                        if (bug.getAssigneeId() != null && bug.getAssigneeId().equals(userId)) {
                            bugState++;
                            System.out.println("匹配到Bug: " + bug.getId());
                        }
                    } catch (NumberFormatException e) {
                        // 用户名不是数字格式，跳过
                        System.out.println("用户名不是数字格式: " + username);
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
                // 更新审批状态
                String action = (String) body.get("action");
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
                
                return Result.success("审批状态更新成功");
            } else {
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
}