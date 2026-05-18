package com.example.springboot.repository;

import com.example.springboot.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManagerId(Long managerId);
    
    @Query("SELECT p FROM Project p WHERE p.team_id = :teamId")
    List<Project> findByTeamId(@Param("teamId") Long teamId);
}
