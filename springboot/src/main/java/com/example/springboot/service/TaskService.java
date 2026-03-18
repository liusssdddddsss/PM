package com.example.springboot.service;

import com.example.springboot.entity.Task;
import com.example.springboot.repository.TaskRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Resource
    TaskRepository taskRepository;

    public List<Task> findall() {
        return taskRepository.findAll();
    }

    public long count() {
        return taskRepository.count();
    }

    // 根据ID获取任务
    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
    }

    // 根据迭代ID获取任务列表
    public List<Task> findByIterationId(Integer iterationId) {
        return taskRepository.findByIterationId(iterationId);
    }

    // 根据项目ID获取任务列表
    public List<Task> findByProjectId(Integer projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    // 根据状态获取任务列表
    public List<Task> findByStatus(Integer status) {
        return taskRepository.findByStatus(status);
    }

    // 保存任务
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    // 删除任务
    public void deleteById(Integer id) {
        taskRepository.deleteById(id);
    }
}
