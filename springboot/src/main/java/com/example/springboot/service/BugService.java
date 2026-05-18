package com.example.springboot.service;

import com.example.springboot.entity.Bug;
import com.example.springboot.repository.BugRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BugService {
    @Resource
    BugRepository bugRepository;

    public List<Bug> findall() {
        return bugRepository.findAll();
    }

    public long count() {
        return bugRepository.count();
    }

    // 根据项目ID获取Bug列表
    public List<Bug> findByProjectId(Integer projectId) {
        return bugRepository.findByProjectId(projectId);
    }
    
    // 保存Bug
    public Bug save(Bug bug) {
        return bugRepository.save(bug);
    }
    
    // 根据ID查找Bug
    public Optional<Bug> findById(Integer id) {
        return bugRepository.findById(id);
    }
}
