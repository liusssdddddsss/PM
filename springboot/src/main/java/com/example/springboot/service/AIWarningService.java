package com.example.springboot.service;

import com.example.springboot.entity.AIWarning;
import com.example.springboot.repository.AIWarningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AIWarningService {

    @Autowired
    private AIWarningRepository aiWarningRepository;

    // 保存AI警告
    public AIWarning save(AIWarning warning) {
        if (warning.getCreatedAt() == null) {
            warning.setCreatedAt(new Date());
        }
        if (warning.getIsRead() == null) {
            warning.setIsRead(0); // 默认未读
        }
        return aiWarningRepository.save(warning);
    }

    // 批量保存AI警告
    public List<AIWarning> saveAll(List<AIWarning> warnings) {
        for (AIWarning warning : warnings) {
            if (warning.getCreatedAt() == null) {
                warning.setCreatedAt(new Date());
            }
            if (warning.getIsRead() == null) {
                warning.setIsRead(0);
            }
        }
        return aiWarningRepository.saveAll(warnings);
    }

    // 获取所有警告
    public List<AIWarning> findAll() {
        return aiWarningRepository.findAll();
    }

    // 根据状态获取警告
    public List<AIWarning> findByIsRead(Integer isRead) {
        return aiWarningRepository.findByIsRead(isRead);
    }

    // 根据类型获取警告
    public List<AIWarning> findByRiskType(String riskType) {
        return aiWarningRepository.findByRiskType(riskType);
    }

    // 根据风险等级获取警告
    public List<AIWarning> findByRiskLevel(String riskLevel) {
        return aiWarningRepository.findByRiskLevel(riskLevel);
    }

    // 根据项目ID获取警告
    public List<AIWarning> findByProjectId(Integer projectId) {
        return aiWarningRepository.findByProjectId(projectId);
    }

    // 标记警告为已读
    public void markAsRead(Long id) {
        AIWarning warning = aiWarningRepository.findById(id).orElse(null);
        if (warning != null) {
            warning.setIsRead(1);
            aiWarningRepository.save(warning);
        }
    }

    // 删除警告
    public void delete(Long id) {
        aiWarningRepository.deleteById(id);
    }
}