package com.example.springboot.repository;

import com.example.springboot.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Integer> {
    // 根据类型查询需求数量
    long countByType(Integer type);
}
