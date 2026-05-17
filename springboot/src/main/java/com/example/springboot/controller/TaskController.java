package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Task;
import com.example.springboot.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "任务相关模块")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(summary = "获取任务列表", description = "根据项目ID获取任务列表")
    @GetMapping
    public Result getTasks(@RequestParam(value = "projectId", required = false) Long projectId) {
        try {
            Iterable<Task> tasks = taskService.findall();
            List<Map<String, Object>> taskList = new ArrayList<>();
            
            for (Task task : tasks) {
                // 如果指定了项目ID，只返回该项目的任务
                if (projectId != null && task.getProjectId() != null) {
                    if (!task.getProjectId().equals(projectId.intValue())) {
                        continue;
                    }
                }
                
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("id", task.getId());
                taskMap.put("name", task.getTitle());
                taskMap.put("projectId", task.getProjectId());
                taskMap.put("status", task.getStatus());
                taskList.add(taskMap);
            }
            
            return Result.success(taskList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }
}
