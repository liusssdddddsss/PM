package com.example.springboot.repository;

import com.example.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    User findById(Integer id);
    
    User findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% OR u.name LIKE %:username%")
    List<User> findByUsernameContainingOrNameContaining(String username);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.is_admin != 1")
    long countNonAdminUsers();
}
