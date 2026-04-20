package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Project;
import com.example.springboot.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // 获取项目列表
    @GetMapping("/project/list")
    public Result getProjectList() {
        try {
            Iterable<Project> projects = projectService.findAll();
            List<Map<String, Object>> projectList = new ArrayList<>();
            
            for (Project project : projects) {
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
}
