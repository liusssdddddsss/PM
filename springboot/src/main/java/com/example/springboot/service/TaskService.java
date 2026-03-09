package com.example.springboot.service;

import com.example.springboot.entity.Task;
import com.example.springboot.repository.TaskRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
