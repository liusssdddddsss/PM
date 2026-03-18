package com.example.springboot.controller;

import com.example.springboot.entity.Iteration;
import com.example.springboot.entity.Task;
import com.example.springboot.service.IterationService;
import com.example.springboot.service.TaskService;
import com.example.springboot.common.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iteration")
public class IterationController {

    @Resource
    private IterationService iterationService;

    @Resource
    private TaskService taskService;

    // 获取所有迭代
    @GetMapping("/list")
    public Result getIterations() {
        try {
            List<Iteration> iterations = iterationService.findAll();
            return Result.success(iterations);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取迭代列表失败: " + e.getMessage());
        }
    }

    // 根据项目ID获取迭代列表
    @GetMapping("/project/{projectId}")
    public Result getIterationsByProjectId(@PathVariable Integer projectId) {
        try {
            List<Iteration> iterations = iterationService.findByProjectId(projectId);
            return Result.success(iterations);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取迭代列表失败: " + e.getMessage());
        }
    }

    // 根据ID获取迭代详情
    @GetMapping("/detail/{id}")
    public Result getIterationById(@PathVariable Integer id) {
        try {
            return iterationService.findById(id)
                    .map(Result::success)
                    .orElse(Result.error(404, "迭代不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取迭代详情失败: " + e.getMessage());
        }
    }

    // 获取迭代的任务列表
    @GetMapping("/tasks/{iterationId}")
    public Result getTasksByIterationId(@PathVariable Integer iterationId) {
        try {
            List<Task> tasks = taskService.findByIterationId(iterationId);
            return Result.success(tasks);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取迭代任务列表失败: " + e.getMessage());
        }
    }

    // 创建迭代
    @PostMapping("/create")
    public Result createIteration(@RequestBody Iteration iteration) {
        try {
            Iteration savedIteration = iterationService.save(iteration);
            return Result.success(savedIteration);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建迭代失败: " + e.getMessage());
        }
    }

    // 更新迭代
    @PutMapping("/update")
    public Result updateIteration(@RequestBody Iteration iteration) {
        try {
            Iteration updatedIteration = iterationService.save(iteration);
            return Result.success(updatedIteration);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新迭代失败: " + e.getMessage());
        }
    }

    // 删除迭代
    @DeleteMapping("/delete/{id}")
    public Result deleteIteration(@PathVariable Integer id) {
        try {
            iterationService.deleteById(id);
            return Result.success("迭代删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除迭代失败: " + e.getMessage());
        }
    }

}
