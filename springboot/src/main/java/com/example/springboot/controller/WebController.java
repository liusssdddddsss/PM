package com.example.springboot.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;

@RestController
public class WebController {
    @GetMapping("/")
    public ResponseEntity<byte[]> index() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        byte[] content = Files.readAllBytes(resource.getFile().toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(content);
    }
    
    // 处理前端路由路径，转发到index.html
    // 只处理前端路由，不拦截API请求
    @GetMapping({"/workbench", "/workbench/**", "/itemSet", "/itemSet/**", "/products", "/products/**", "/tasks", "/tasks/**", "/iterations", "/iterations/**", "/teams", "/teams/**", "/test", "/test/**", "/feedback", "/feedback/**", "/AI", "/AI/**"})
    public ResponseEntity<byte[]> handleFrontendRoutes(HttpServletRequest request) throws IOException {
        // 检查是否是API请求（包含/api/或特定的API路径）
        String requestPath = request.getRequestURI();
        if (requestPath.contains("/api/") || requestPath.contains("/teams/overview") || requestPath.contains("/teams/my-teams") || requestPath.contains("/teams/messages") || requestPath.contains("/teams/create") || requestPath.contains("/teams/invite") || requestPath.contains("/teams/division/edit") || requestPath.contains("/teams/tasks") || requestPath.contains("/teams/statistics") || requestPath.contains("/teams/personal-tasks") || requestPath.contains("/teams/disband") || requestPath.contains("/teams/remove-member") || requestPath.contains("/dashboard/") || requestPath.startsWith("/workbench/tasks")) {
            // 如果是API请求，不处理，让其他控制器处理
            throw new IllegalStateException("API request");
        }
        
        ClassPathResource resource = new ClassPathResource("static/index.html");
        byte[] content = Files.readAllBytes(resource.getFile().toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
