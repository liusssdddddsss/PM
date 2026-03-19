package com.example.springboot.repository;

import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    
    // 根据创建者ID查询团队
    List<Team> findByCreatorId(Long creatorId);
    
    // 查询用户加入的所有团队（通过team_members表关联）
    @Query("SELECT t FROM Team t JOIN TeamMember tm ON t.id = tm.teamId WHERE tm.userId = :userId")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);
}
