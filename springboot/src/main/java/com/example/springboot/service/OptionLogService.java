package com.example.springboot.service;

import com.example.springboot.entity.OptionLog;
import com.example.springboot.repository.OptionLogRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OptionLogService {

    @Resource
    private OptionLogRepository optionLogRepository;

    /**
     * 记录一条操作日志
     */
    public OptionLog log(String operator, String action, String link) {
        OptionLog log = new OptionLog();
        log.setOperator(operator);
        log.setAction(action);
        log.setLink(link);
        log.setCreatedAt(new Date());
        return optionLogRepository.save(log);
    }

    /**
     * 保存已经构造好的实体
     */
    public OptionLog save(OptionLog log) {
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(new Date());
        }
        return optionLogRepository.save(log);
    }

    /**
     * 获取最近的操作日志
     */
    public List<OptionLog> findLatest() {
        return optionLogRepository.findTop20ByOrderByCreatedAtDesc();
    }
}

