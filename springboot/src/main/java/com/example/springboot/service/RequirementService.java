package com.example.springboot.service;

import com.example.springboot.entity.Requirement;
import com.example.springboot.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequirementService {
    @Autowired
    private RequirementRepository requirementRepository;

    public Optional<Requirement> findById(Integer id) {
        return requirementRepository.findById(id);
    }

    public Requirement save(Requirement requirement) {
        return requirementRepository.save(requirement);
    }

    public void deleteById(Integer id) {
        requirementRepository.deleteById(id);
    }

    public Iterable<Requirement> findAll() {
        return requirementRepository.findAll();
    }
}
