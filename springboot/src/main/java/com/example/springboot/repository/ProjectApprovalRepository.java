package com.example.springboot.repository;

import com.example.springboot.entity.ProjectApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectApprovalRepository extends JpaRepository<ProjectApproval, Integer> {
    @Query("SELECT p FROM ProjectApproval p WHERE p.feedback_id = ?1")
    ProjectApproval findByFeedbackId(Long feedbackId);
}
