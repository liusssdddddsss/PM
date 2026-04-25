package com.example.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class SpringbootApplication {

    public static void main(String[] args) {
        org.springframework.context.ConfigurableApplicationContext context = SpringApplication.run(SpringbootApplication.class, args);
        
        // 查看数据库中的统计数据
        com.example.springboot.service.TeamService teamService = context.getBean(com.example.springboot.service.TeamService.class);
        com.example.springboot.service.TeamMemberService teamMemberService = context.getBean(com.example.springboot.service.TeamMemberService.class);
        com.example.springboot.service.ProjectService projectService = context.getBean(com.example.springboot.service.ProjectService.class);
        com.example.springboot.repository.TaskRepository taskRepository = context.getBean(com.example.springboot.repository.TaskRepository.class);
        
        long teamCount = teamService.count();
        long memberCount = teamMemberService.count();
        long projectCount = projectService.count();
        long taskCount = taskRepository.count();
        
        System.out.println("数据库中的实际统计数据:");
        System.out.println("团队数量: " + teamCount);
        System.out.println("团队成员数量: " + memberCount);
        System.out.println("项目数量: " + projectCount);
        System.out.println("任务数量: " + taskCount);
        
        // 查看前10个任务的详细信息
        System.out.println("\n前10个任务的详细信息:");
        taskRepository.findAll().stream().limit(10).forEach(task -> {
            System.out.println("任务ID: " + task.getId() + ", 项目ID: " + task.getProjectId() + ", 负责人ID: " + task.getAssigneeId() + ", 创建者ID: " + task.getCreatorId());
        });
        
        // 检查202201用户的任务和Bug
        System.out.println("\n检查202201用户的任务和Bug:");
        com.example.springboot.service.UserService userService = context.getBean(com.example.springboot.service.UserService.class);
        com.example.springboot.service.BugService bugService = context.getBean(com.example.springboot.service.BugService.class);
        
        // 查找202201用户
        com.example.springboot.entity.User user = userService.findByUsername("202201");
        if (user != null) {
            System.out.println("找到用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            // 检查任务
            int userTaskCount = 0;
            for (com.example.springboot.entity.Task task : taskRepository.findAll()) {
                if (task.getAssigneeId() != null && task.getAssigneeId().equals(user.getId())) {
                    userTaskCount++;
                    System.out.println("任务ID: " + task.getId() + ", 名称: " + task.getTitle());
                }
            }
            System.out.println("用户202201的任务数量: " + userTaskCount);
            
            // 检查Bug
            int userBugCount = 0;
            for (com.example.springboot.entity.Bug bug : bugService.findall()) {
                if (bug.getAssigneeId() != null && bug.getAssigneeId().equals(user.getId())) {
                    userBugCount++;
                    System.out.println("Bug ID: " + bug.getId() + ", 标题: " + bug.getTitle());
                }
            }
            System.out.println("用户202201的Bug数量: " + userBugCount);
        } else {
            System.out.println("未找到用户202201");
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "http://localhost:3001", "http://localhost:3002")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
            
            @Override
            public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:///D:/uploads/");
            }
        };
    }

}
