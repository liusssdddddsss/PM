package com.example.springboot.repository;

import com.example.springboot.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    @Query("SELECT l FROM LoginLog l ORDER BY l.loginTime DESC")
    List<LoginLog> findAllByOrderByLoginTimeDesc();
    
    @Query("SELECT l FROM LoginLog l WHERE l.user_id = ?1 ORDER BY l.loginTime DESC")
    List<LoginLog> findByUser_idOrderByLoginTimeDesc(String user_id);
    
    long count();
    
    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.loginTime > ?1")
    long countByLoginTimeAfter(java.util.Date date);
    
    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.status = ?1")
    long countByStatus(int status);
}
