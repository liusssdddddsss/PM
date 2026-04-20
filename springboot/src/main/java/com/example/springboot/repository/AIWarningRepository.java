package com.example.springboot.repository;

import com.example.springboot.entity.AIWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIWarningRepository extends JpaRepository<AIWarning, Long> {
    List<AIWarning> findByIsRead(Integer isRead);
    List<AIWarning> findByRiskType(String riskType);
    List<AIWarning> findByRiskLevel(String riskLevel);
    List<AIWarning> findByProjectId(Integer projectId);
}