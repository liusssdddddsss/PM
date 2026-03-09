package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Bug;
import com.example.springboot.entity.ProjectApproval;
import com.example.springboot.entity.Requirement;
import com.example.springboot.entity.Task;
import com.example.springboot.service.BugService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.RequirementService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return Result.success(tasks);
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
}