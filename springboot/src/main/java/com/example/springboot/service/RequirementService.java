package com.example.springboot.service;

import com.example.springboot.entity.Requirement;
import com.example.springboot.repository.RequirementRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequirementService {
    @Resource
    RequirementRepository requirementRepository;

    public List<Requirement> findall() {
        return requirementRepository.findAll();
    }

    public long count() {
        return requirementRepository.count();
    }

    public long countByType(Integer type) {
        return requirementRepository.countByType(type);
    }
}
