package com.example.springboot.service;

import com.example.springboot.entity.Team;
import com.example.springboot.repository.TeamRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    
    @Resource
    private TeamRepository teamRepository;
    
    // 根据ID查询团队
    public Optional<Team> findById(Integer id) {
        return teamRepository.findById(id);
    }
    
    // 查询所有团队
    public List<Team> findAll() {
        return teamRepository.findAll();
    }
    
    // 根据创建者ID查询团队
    public List<Team> findByCreatorId(Long creatorId) {
        return teamRepository.findByCreatorId(creatorId);
    }
    
    // 查询用户加入的所有团队
    public List<Team> findTeamsByUserId(Long userId) {
        return teamRepository.findTeamsByUserId(userId);
    }
    
    // 保存团队
    public Team save(Team team) {
        return teamRepository.save(team);
    }
    
    // 删除团队
    public void deleteById(Integer id) {
        teamRepository.deleteById(id);
    }
}
