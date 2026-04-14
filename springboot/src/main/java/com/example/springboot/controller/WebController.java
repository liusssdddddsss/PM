package com.example.springboot.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    // 处理所有前端路由路径，转发到index.html
    @GetMapping("/**")
    public ResponseEntity<byte[]> handleFrontendRoutes() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        byte[] content = Files.readAllBytes(resource.getFile().toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
