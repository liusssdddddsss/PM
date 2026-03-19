package com.example.springboot.repository;

import com.example.springboot.entity.OptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionLogRepository extends JpaRepository<OptionLog, Long> {

    /**
     * 获取最新的操作日志
     */
    List<OptionLog> findTop20ByOrderByCreatedAtDesc();
}

