package com.example.springboot.repository;

import com.example.springboot.entity.Iteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IterationRepository extends JpaRepository<Iteration, Integer> {

    // 根据项目ID查询迭代列表
    List<Iteration> findByProjectId(Integer projectId);

    // 根据状态查询迭代列表
    List<Iteration> findByStatus(Integer status);

    // 根据项目ID和状态查询迭代列表
    List<Iteration> findByProjectIdAndStatus(Integer projectId, Integer status);

}
