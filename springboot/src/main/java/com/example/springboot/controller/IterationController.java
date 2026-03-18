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
    public Result<List<Iteration>> getIterations() {
        List<Iteration> iterations = iterationService.findAll();
        return Result.success(iterations);
    }

    // 根据项目ID获取迭代列表
    @GetMapping("/project/{projectId}")
    public Result<List<Iteration>> getIterationsByProjectId(@PathVariable Integer projectId) {
        List<Iteration> iterations = iterationService.findByProjectId(projectId);
        return Result.success(iterations);
    }

    // 根据ID获取迭代详情
    @GetMapping("/detail/{id}")
    public Result<Iteration> getIterationById(@PathVariable Integer id) {
        return iterationService.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "迭代不存在"));
    }

    // 获取迭代的任务列表
    @GetMapping("/tasks/{iterationId}")
    public Result<List<Task>> getTasksByIterationId(@PathVariable Integer iterationId) {
        List<Task> tasks = taskService.findByIterationId(iterationId);
        return Result.success(tasks);
    }

    // 创建迭代
    @PostMapping("/create")
    public Result<Iteration> createIteration(@RequestBody Iteration iteration) {
        Iteration savedIteration = iterationService.save(iteration);
        return Result.success(savedIteration);
    }

    // 更新迭代
    @PutMapping("/update")
    public Result<Iteration> updateIteration(@RequestBody Iteration iteration) {
        Iteration updatedIteration = iterationService.save(iteration);
        return Result.success(updatedIteration);
    }

    // 删除迭代
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteIteration(@PathVariable Integer id) {
        iterationService.deleteById(id);
        return Result.success("迭代删除成功");
    }

}
