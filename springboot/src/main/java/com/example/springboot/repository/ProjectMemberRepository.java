package com.example.springboot.repository;

import com.example.springboot.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByUserId(Long user_id);
    List<ProjectMember> findByProjectId(Long projectId);
}
