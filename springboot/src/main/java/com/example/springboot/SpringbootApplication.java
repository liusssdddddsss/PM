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
