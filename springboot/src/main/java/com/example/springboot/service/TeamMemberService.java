package com.example.springboot.service;

import com.example.springboot.entity.TeamMember;
import com.example.springboot.repository.TeamMemberRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberService {
    
    @Resource
    private TeamMemberRepository teamMemberRepository;
    
    // 根据ID查询团队成员
    public TeamMember findById(Integer id) {
        return teamMemberRepository.findById(id).orElse(null);
    }
    
    // 根据团队ID查询成员
    public List<TeamMember> findByTeamId(Integer teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }
    
    // 根据用户ID查询加入的团队成员记录
    public List<TeamMember> findByUserId(Long userId) {
        return teamMemberRepository.findByUserId(userId);
    }
    
    // 根据团队ID和用户ID查询成员记录
    public TeamMember findByTeamIdAndUserId(Integer teamId, Long userId) {
        return teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
    }
    
    // 保存团队成员
    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }
    
    // 删除团队成员
    public void deleteById(Integer id) {
        teamMemberRepository.deleteById(id);
    }
}
