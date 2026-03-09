package com.example.springboot.repository;

import com.example.springboot.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugRepository extends JpaRepository<Bug, Integer> {
    // 可以添加自定义查询方法
}
