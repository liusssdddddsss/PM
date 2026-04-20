package com.example.springboot.repository;

import com.example.springboot.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Integer> {
    // 根据项目ID查询Bug列表
    List<Bug> findByProjectId(Integer projectId);
}
