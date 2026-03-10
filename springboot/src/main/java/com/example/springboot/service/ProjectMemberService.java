package com.example.springboot.service;

import com.example.springboot.entity.ProjectMember;
import com.example.springboot.repository.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberService {
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public List<ProjectMember> findByUserId(Long userId) {
        return projectMemberRepository.findByUserId(userId);
    }
}
