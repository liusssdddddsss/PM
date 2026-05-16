package com.example.springboot.controller;

import com.example.springboot.entity.Bug;
import com.example.springboot.repository.BugRepository;
import com.example.springboot.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private BugService bugService;

    // 更新Bug
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateBug(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer bugId = (Integer) request.get("bugId");
            Optional<Bug> bugOptional = bugRepository.findById(bugId);
            
            if (bugOptional.isPresent()) {
                Bug bug = bugOptional.get();
                
                // 更新字段
                if (request.containsKey("name")) {
                    bug.setTitle((String) request.get("name"));
                }
                if (request.containsKey("projectName")) {
                    bug.setProjectName((String) request.get("projectName"));
                }
                if (request.containsKey("priority")) {
                    String priority = (String) request.get("priority");
                    switch (priority) {
                        case "紧急":
                            bug.setSeverity(1);
                            break;
                        case "一般":
                            bug.setSeverity(2);
                            break;
                        case "正常":
                            bug.setSeverity(3);
                            break;
                        default:
                            bug.setSeverity(3);
                    }
                }
                if (request.containsKey("status")) {
                    String status = (String) request.get("status");
                    switch (status) {
                        case "待处理":
                            bug.setStatus(0);
                            break;
                        case "处理中":
                            bug.setStatus(1);
                            break;
                        case "已解决":
                            bug.setStatus(2);
                            break;
                        default:
                            bug.setStatus(0);
                    }
                }
                if (request.containsKey("deadline")) {
                    bug.setDeadline((String) request.get("deadline"));
                }
                if (request.containsKey("description")) {
                    bug.setDescription((String) request.get("description"));
                }
                
                bugRepository.save(bug);
                
                response.put("code", 200);
                response.put("message", "Bug更新成功");
            } else {
                response.put("code", 404);
                response.put("message", "Bug不存在");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新Bug失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }

    // 解决Bug
    @PostMapping("/resolve")
    public ResponseEntity<Map<String, Object>> resolveBug(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer bugId = (Integer) request.get("bugId");
            Optional<Bug> bugOptional = bugRepository.findById(bugId);
            
            if (bugOptional.isPresent()) {
                Bug bug = bugOptional.get();
                bug.setStatus(2); // 设置为已解决状态
                
                if (request.containsKey("solution")) {
                    bug.setSolution((String) request.get("solution"));
                }
                if (request.containsKey("resolveTime")) {
                    bug.setResolvedAt((String) request.get("resolveTime"));
                }
                
                bugRepository.save(bug);
                
                response.put("code", 200);
                response.put("message", "Bug解决成功");
            } else {
                response.put("code", 404);
                response.put("message", "Bug不存在");
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "解决Bug失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }

    // 删除Bug
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBug(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("删除Bug请求 - ID: " + id);
            
            if (bugRepository.existsById(id)) {
                bugRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "Bug删除成功");
                System.out.println("Bug删除成功 - ID: " + id);
            } else {
                response.put("code", 404);
                response.put("message", "Bug不存在");
                System.out.println("Bug不存在 - ID: " + id);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除Bug失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }
}