package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Project;
import com.example.springboot.service.ProjectProgressService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.utils.RolePermissionUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private RolePermissionUtils rolePermissionUtils;
    
    @Autowired
    private ProjectProgressService projectProgressService;

    // 获取项目列表
    @GetMapping("/project/list")
    public Result getProjectList(@RequestParam(value = "username", required = false) String username) {
        try {
            Iterable<Project> projects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : projects) {
                // 检查用户是否有权限访问该项目
                if (username != null && !rolePermissionUtils.hasProjectAccess(username, project.getId())) {
                    continue; // 跳过无权限的项目
                }
                
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("id", project.getId());
                projectMap.put("name", project.getName());
                projectMap.put("status", project.getStatus());
                projectMap.put("progress", project.getProgress());
                projectMap.put("startDate", project.getStart_date());
                projectMap.put("endDate", project.getEnd_date());
                projectMap.put("managerId", project.getManagerId());
                projectMap.put("productId", project.getProduct_id());
                projectList.add(projectMap);
            }
            
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }
    
    // 获取所有项目列表（简单版，供前端下拉选择使用）
    @GetMapping("/projects")
    public Result getAllProjects() {
        try {
            Iterable<Project> projects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : projects) {
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("id", project.getId());
                projectMap.put("name", project.getName());
                projectMap.put("product_id", project.getProduct_id());
                projectList.add(projectMap);
            }
            
            return Result.success(projectList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "计算并更新单个项目的进度", description = "根据项目ID计算任务完成进度并更新到数据库")
    @PostMapping("/project/calculate-progress")
    public Result calculateProjectProgress(@RequestParam("projectId") Long projectId) {
        try {
            int progress = projectProgressService.calculateAndUpdateProjectProgress(projectId);
            Map<String, Object> result = new HashMap<>();
            result.put("projectId", projectId);
            result.put("progress", progress);
            result.put("message", "项目进度计算完成");
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("计算项目进度失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "计算并更新所有项目的进度", description = "批量计算所有项目的任务完成进度并更新到数据库")
    @PostMapping("/project/calculate-all-progress")
    public Result calculateAllProjectsProgress() {
        try {
            projectProgressService.calculateAndUpdateAllProjectsProgress();
            return Result.success("所有项目进度计算完成");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("计算所有项目进度失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取项目当前进度", description = "获取项目当前的任务完成进度（不更新数据库）")
    @GetMapping("/project/progress")
    public Result getProjectProgress(@RequestParam("projectId") Long projectId) {
        try {
            int progress = projectProgressService.getProjectProgress(projectId);
            Map<String, Object> result = new HashMap<>();
            result.put("projectId", projectId);
            result.put("progress", progress);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取项目进度失败: " + e.getMessage());
        }
    }
}
