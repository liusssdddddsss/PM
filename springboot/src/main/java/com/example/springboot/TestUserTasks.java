package com.example.springboot;

import com.example.springboot.entity.Task;
import com.example.springboot.entity.Bug;
import com.example.springboot.service.UserService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.BugService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestUserTasks {
    public static void main(String[] args) {
        // 启动Spring Boot应用
        ConfigurableApplicationContext context = SpringApplication.run(SpringbootApplication.class, args);
        
        // 获取服务
        UserService userService = context.getBean(UserService.class);
        TaskService taskService = context.getBean(TaskService.class);
        BugService bugService = context.getBean(BugService.class);
        
        // 查找202201用户
        System.out.println("查找用户202201...");
        com.example.springboot.entity.User user = userService.findByUsername("202201");
        
        if (user != null) {
            System.out.println("找到用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            // 检查任务
            System.out.println("\n检查用户的任务...");
            int userTaskCount = 0;
            for (Task task : taskService.findall()) {
                if (task.getAssigneeId() != null && task.getAssigneeId().equals(user.getId())) {
                    userTaskCount++;
                    System.out.println("任务ID: " + task.getId() + ", 标题: " + task.getTitle());
                }
            }
            System.out.println("用户202201的任务数量: " + userTaskCount);
            
            // 检查Bug
            System.out.println("\n检查用户的Bug...");
            int userBugCount = 0;
            for (Bug bug : bugService.findall()) {
                if (bug.getAssigneeId() != null && bug.getAssigneeId().equals(user.getId())) {
                    userBugCount++;
                    System.out.println("Bug ID: " + bug.getId() + ", 标题: " + bug.getTitle());
                }
            }
            System.out.println("用户202201的Bug数量: " + userBugCount);
        } else {
            System.out.println("未找到用户202201");
        }
        
        // 关闭应用
        context.close();
    }
}