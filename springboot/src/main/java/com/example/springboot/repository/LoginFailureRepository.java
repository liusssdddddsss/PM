package com.example.springboot.repository;

import com.example.springboot.entity.LoginFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginFailureRepository extends JpaRepository<LoginFailure, Long> {
    Optional<LoginFailure> findByUsername(String username);
}
