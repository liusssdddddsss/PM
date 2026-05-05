package com.example.springboot.repository;

import com.example.springboot.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // 统计项目下的总任务数
    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId")
    long countByProjectId(@Param("projectId") Integer projectId);
    
    // 统计项目下已完成的任务数（状态为已完成，假设状态值为3）
    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId AND t.status = 3")
    long countCompletedByProjectId(@Param("projectId") Integer projectId);

}
