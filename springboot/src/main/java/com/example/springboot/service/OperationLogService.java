package com.example.springboot.service;

import com.example.springboot.entity.OperationLog;
import com.example.springboot.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    // 获取所有操作日志
    public List<OperationLog> findall() {
        return operationLogRepository.findAll();
    }

    // 保存操作日志
    public void save(OperationLog operationLog) {
        operationLogRepository.save(operationLog);
    }

    // 根据ID查找操作日志
    public OperationLog findById(Long id) {
        return operationLogRepository.findById(id).orElse(null);
    }

    // 删除操作日志
    public void deleteById(Long id) {
        operationLogRepository.deleteById(id);
    }
}
