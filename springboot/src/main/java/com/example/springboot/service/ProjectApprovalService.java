package com.example.springboot.service;

import com.example.springboot.entity.ProjectApproval;
import com.example.springboot.repository.ProjectApprovalRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectApprovalService {
    @Resource
    ProjectApprovalRepository projectApprovalRepository;

    public List<ProjectApproval> findall() {
        return projectApprovalRepository.findAll();
    }

    public long count() {
        return projectApprovalRepository.count();
    }

    public ProjectApproval save(ProjectApproval projectApproval) {
        return projectApprovalRepository.save(projectApproval);
    }
}
