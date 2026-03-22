package com.example.springboot.repository;

import com.example.springboot.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    
    // 根据团队ID查询成员
    List<TeamMember> findByTeamId(Integer teamId);
    
    // 根据用户ID查询加入的团队成员记录
    List<TeamMember> findByUserId(Long userId);
    
    // 根据团队ID和用户ID查询成员记录
    TeamMember findByTeamIdAndUserId(Integer teamId, Long userId);
    
    // 根据团队ID删除成员
    void deleteByTeamId(Integer teamId);
}
