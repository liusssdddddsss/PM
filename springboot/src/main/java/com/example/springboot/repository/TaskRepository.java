package com.example.springboot.repository;

import com.example.springboot.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // 根据迭代ID查询任务列表
    List<Task> findByIterationId(Integer iterationId);

    // 根据项目ID查询任务列表
    List<Task> findByProjectId(Integer projectId);

    // 根据状态查询任务列表
    List<Task> findByStatus(Integer status);
    
    // 根据负责人ID查询任务列表
    List<Task> findByAssigneeId(Integer assigneeId);
    
    // 根据创建者ID查询任务列表
    List<Task> findByCreatorId(Integer creatorId);

}
