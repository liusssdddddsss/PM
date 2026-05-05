package com.example.springboot.service;

import com.example.springboot.entity.Project;
import com.example.springboot.repository.ProjectRepository;
import com.example.springboot.repository.TaskRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectProgressService {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private ProjectRepository projectRepository;

    /**
     * 计算并更新单个项目的进度
     * 进度 = (已完成任务数 / 总任务数) * 100
     *
     * @param projectId 项目ID
     * @return 更新后的进度百分比
     */
    @Transactional
    public int calculateAndUpdateProjectProgress(Long projectId) {
        System.out.println("开始计算项目进度，项目ID: " + projectId);
        
        // 统计该项目下的总任务数
        long totalTasks = taskRepository.countByProjectId(projectId.intValue());
        System.out.println("项目 " + projectId + " 总任务数: " + totalTasks);
        
        // 统计该项目下已完成的任务数
        long completedTasks = taskRepository.countCompletedByProjectId(projectId.intValue());
        System.out.println("项目 " + projectId + " 已完成任务数: " + completedTasks);
        
        // 计算进度百分比
        final int progress;
        if (totalTasks > 0) {
            progress = (int) ((completedTasks * 100) / totalTasks);
            System.out.println("计算出的进度: " + progress + "%");
        } else {
            progress = 0;
        }
        
        // 更新项目进度到数据库
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null) {
            project.setProgress(progress);
            projectRepository.save(project);
            System.out.println("项目 " + projectId + " 进度已更新为: " + progress + "%");
        }
        
        return progress;
    }

    /**
     * 计算并更新所有项目的进度
     */
    @Transactional
    public void calculateAndUpdateAllProjectsProgress() {
        System.out.println("开始计算所有项目的进度");
        
        // 获取所有项目
        projectRepository.findAll().forEach(project -> {
            calculateAndUpdateProjectProgress(project.getId());
        });
        
        System.out.println("所有项目进度计算完成");
    }

    /**
     * 当任务状态改变时，自动更新所属项目的进度
     *
     * @param taskProjectId 任务所属的项目ID
     * @return 更新后的进度百分比
     */
    @Transactional
    public int updateProgressOnTaskStatusChange(Integer taskProjectId) {
        if (taskProjectId == null) {
            System.out.println("任务项目ID为空，跳过进度更新");
            return 0;
        }
        return calculateAndUpdateProjectProgress(taskProjectId.longValue());
    }

    /**
     * 获取项目当前进度（不更新数据库）
     *
     * @param projectId 项目ID
     * @return 进度百分比
     */
    public int getProjectProgress(Long projectId) {
        long totalTasks = taskRepository.countByProjectId(projectId.intValue());
        if (totalTasks == 0) {
            return 0;
        }
        long completedTasks = taskRepository.countCompletedByProjectId(projectId.intValue());
        return (int) ((completedTasks * 100) / totalTasks);
    }
}