package com.example.springboot.repository;

import com.example.springboot.entity.ProjectApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectApprovalRepository extends JpaRepository<ProjectApproval, Integer> {
    // 可以添加自定义查询方法
}
