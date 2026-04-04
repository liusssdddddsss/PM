package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.ProjectApproval;
import com.example.springboot.entity.Requirement;
import com.example.springboot.entity.Task;
import com.example.springboot.service.BugService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.RequirementService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.UserService;
import com.example.springboot.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workbench")
@Tag(name="工作台列表模块")
public class WorkbenchController {

    @Resource
    ProjectApprovalService projectApprovalService;
    @Resource
    TaskService taskService;
    @Resource
    RequirementService requirementService;
    @Resource
    BugService bugService;
    @Resource
    ProjectService projectService;
    @Resource
    UserService userService;
    @Resource
    ProjectMemberService projectMemberService;

    @Operation(summary = "获取审批列表", description = "返回审批列表数据")
    @GetMapping("/approvals")
    public Result getApprovals() {
        try {
            List<ProjectApproval> approvals = projectApprovalService.findall();
            List<Map<String, Object>> approvalList = new ArrayList<>();
            
            for (ProjectApproval approval : approvals) {
                Map<String, Object> approvalMap = new HashMap<>();
                approvalMap.put("id", approval.getId());
                approvalMap.put("project_id", approval.getProject_id());
                approvalMap.put("approver_id", approval.getApprover_id());
                approvalMap.put("action", approval.getAction());
                approvalMap.put("type", approval.getType());
                approvalMap.put("comment", approval.getComment());
                approvalMap.put("created_at", approval.getCreated_at());
                
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
                
                approvalList.add(approvalMap);
            }
            
            return Result.success(approvalList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取审批列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取任务列表", description = "返回任务列表数据")
    @GetMapping("/tasks")
    public Result getTasks() {
        try {
            List<Task> tasks = taskService.findall();
            List<Map<String, Object>> taskList = new ArrayList<>();
            
            for (Task task : tasks) {
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
            }
            
            return Result.success(taskList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取研发需求列表", description = "返回研发需求列表数据")
    @GetMapping("/research-needs")
    public Result getResearchNeeds() {
        try {
            List<Requirement> requirements = requirementService.findall();
            // 过滤出type=1的研发需求
            List<Requirement> researchNeeds = requirements.stream()
                    .filter(req -> req.getType() != null && req.getType() == 1)
                    .toList();
            return Result.success(researchNeeds);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取研发需求列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取用户需求列表", description = "返回用户需求列表数据")
    @GetMapping("/user-needs")
    public Result getUserNeeds() {
        try {
            List<Requirement> requirements = requirementService.findall();
            // 过滤出type=2的用户需求
            List<Requirement> userNeeds = requirements.stream()
                    .filter(req -> req.getType() != null && req.getType() == 2)
                    .toList();
            return Result.success(userNeeds);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户需求列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取Bug列表", description = "返回Bug列表数据")
    @GetMapping("/bugs")
    public Result getBugs() {
        try {
            List<Bug> bugs = bugService.findall();
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
            // 首先根据用户名获取用户信息
            var user = userService.findById(username);
            if (user.isPresent()) {
                Long userId = Long.parseLong(user.get().getUsername());
                
                // 根据用户ID查询参与的项目成员记录
                List<com.example.springboot.entity.ProjectMember> projectMembers = projectMemberService.findByUserId(userId);
                List<Map<String, Object>> projectList = new ArrayList<>();
                
                // 遍历项目成员记录，获取对应的项目信息
                for (com.example.springboot.entity.ProjectMember member : projectMembers) {
                    Long projectId = member.getProjectId();
                    if (projectId != null) {
                        var project = projectService.findById(projectId);
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
                            projectList.add(projectMap);
                        }
                    }
                }
                
                return Result.success(projectList);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取未完成项目列表", description = "返回未完成项目列表数据")
    @GetMapping("/unfinished-projects")
    public Result getUnfinishedProjects() {
        try {
            // 获取所有项目
            Iterable<Project> allProjects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : allProjects) {
                // 过滤出未完成的项目（状态不是2已关闭或3已归档）
                if (project.getStatus() == null || (project.getStatus() != 2 && project.getStatus() != 3)) {
                    Map<String, Object> projectMap = new HashMap<>();
                    projectMap.put("title", project.getName());
                    
                    // 获取负责人姓名
                    String managerName = "未知"; 
                    if (project.getManager_id() != null) {
                        try {
                            var user = userService.findById(project.getManager_id().toString());
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
                    
                    // 计算剩余需求数量（从requirements表中获取）
                    int remainingNeeds = 0;
                    List<Requirement> requirements = requirementService.findall();
                    for (Requirement req : requirements) {
                        if (req.getProject_id() != null && req.getProject_id().equals(projectId)) {
                            // 假设需求状态为0表示未完成
                            if (req.getStatus() == null || req.getStatus() == 0) {
                                remainingNeeds++;
                            }
                        }
                    }
                    projectMap.put("shengYuNeeds", remainingNeeds);
                    
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
                        if (bug.getProject_id() != null && bug.getProject_id().equals(projectId)) {
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
            
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未完成项目列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取所有项目列表", description = "返回所有项目列表数据")
    @GetMapping("/all-projects")
    public Result getAllProjects() {
        try {
            // 获取所有项目
            Iterable<Project> allProjects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : allProjects) {
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("id", project.getId());
                projectMap.put("title", project.getName());
                
                // 获取负责人姓名
                String managerName = "未知"; 
                if (project.getManager_id() != null) {
                    try {
                        var user = userService.findById(project.getManager_id().toString());
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
            
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除项目", description = "根据ID删除项目")
    @DeleteMapping("/projects/{id}")
    public Result deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteById(id);
            return Result.success("删除项目成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除项目失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增项目", description = "新增项目")
    @PostMapping("/projects")
    public Result addProject(@RequestBody Project project) {
        try {
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
}