package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findById(String id) {
        try {
            // 尝试将id转换为Integer，使用UserRepository的findById方法查找用户
            Integer userId = Integer.parseInt(id);
            User user = userRepository.findById(userId);
            if (user != null) {
                return Optional.of(user);
            }
        } catch (NumberFormatException e) {
            // id不是数字格式，尝试查找username字段匹配的用户
            User userByUsername = userRepository.findByUsername(id);
            if (userByUsername != null) {
                return Optional.of(userByUsername);
            }
        }
        
        // 如果没有找到，尝试遍历所有用户，查找任何匹配的字段
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            // 检查id字段
            if (user.getId() != null && user.getId().toString().equals(id)) {
                return Optional.of(user);
            }
            // 检查username字段
            if (user.getUsername() != null && user.getUsername().equals(id)) {
                return Optional.of(user);
            }
            // 检查name字段
            if (user.getName() != null && user.getName().equals(id)) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingOrNameContaining(keyword);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
}
