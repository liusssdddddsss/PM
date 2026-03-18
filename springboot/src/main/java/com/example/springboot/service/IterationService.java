package com.example.springboot.service;

import com.example.springboot.entity.Iteration;
import com.example.springboot.repository.IterationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IterationService {

    @Autowired
    private IterationRepository iterationRepository;

    // 获取所有迭代
    public List<Iteration> findAll() {
        return iterationRepository.findAll();
    }

    // 根据ID获取迭代
    public Optional<Iteration> findById(Integer id) {
        return iterationRepository.findById(id);
    }

    // 根据项目ID获取迭代列表
    public List<Iteration> findByProjectId(Integer projectId) {
        return iterationRepository.findByProjectId(projectId);
    }

    // 根据状态获取迭代列表
    public List<Iteration> findByStatus(Integer status) {
        return iterationRepository.findByStatus(status);
    }

    // 根据项目ID和状态获取迭代列表
    public List<Iteration> findByProjectIdAndStatus(Integer projectId, Integer status) {
        return iterationRepository.findByProjectIdAndStatus(projectId, status);
    }

    // 保存迭代
    public Iteration save(Iteration iteration) {
        return iterationRepository.save(iteration);
    }

    // 删除迭代
    public void deleteById(Integer id) {
        iterationRepository.deleteById(id);
    }

}
