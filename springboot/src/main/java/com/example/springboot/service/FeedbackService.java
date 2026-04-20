package com.example.springboot.service;

import com.example.springboot.entity.Feedback;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.User;
import com.example.springboot.repository.FeedbackRepository;
import com.example.springboot.repository.TeamRepository;
import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;

    public List<FeedbackWithAssigneeName> findAll() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        List<FeedbackWithAssigneeName> result = new ArrayList<>();
        
        for (Feedback feedback : feedbacks) {
            FeedbackWithAssigneeName item = new FeedbackWithAssigneeName(feedback);
            if (feedback.getAssigneeId() != null) {
                userRepository.findById(feedback.getAssigneeId().toString()).ifPresent(user -> {
                    item.setAssigneeName(user.getName());
                });
            }
            if (feedback.getCreatorId() != null) {
                userRepository.findById(feedback.getCreatorId().toString()).ifPresent(user -> {
                    item.setCreatorName(user.getName());
                });
                // 查询用户所属团队
                List<Team> teams = teamRepository.findTeamsByUserId(feedback.getCreatorId());
                if (teams != null && !teams.isEmpty()) {
                    item.setTeamId(teams.get(0).getId());
                    item.setTeamName(teams.get(0).getName());
                }
            }
            result.add(item);
        }
        return result;
    }

    public FeedbackWithAssigneeName findById(Long id) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        if (feedback == null) {
            return null;
        }
        
        FeedbackWithAssigneeName result = new FeedbackWithAssigneeName(feedback);
        if (feedback.getAssigneeId() != null) {
            userRepository.findById(feedback.getAssigneeId().toString()).ifPresent(user -> {
                result.setAssigneeName(user.getName());
            });
        }
        if (feedback.getCreatorId() != null) {
            userRepository.findById(feedback.getCreatorId().toString()).ifPresent(user -> {
                result.setCreatorName(user.getName());
            });
            // 查询用户所属团队
            List<Team> teams = teamRepository.findTeamsByUserId(feedback.getCreatorId());
            if (teams != null && !teams.isEmpty()) {
                result.setTeamId(teams.get(0).getId());
                result.setTeamName(teams.get(0).getName());
            }
        }
        return result;
    }

    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public void deleteById(Long id) {
        feedbackRepository.deleteById(id);
    }

    // 内部类，用于扩展Feedback，添加assigneeName字段
    public static class FeedbackWithAssigneeName {
        private Long id;
        private String title;
        private String description;
        private Integer priority;
        private String status;
        private String type;
        private Long assigneeId;
        private String assigneeName;
        private String solution;
        private Long creatorId;
        private String creatorName;
        private Integer teamId;
        private String teamName;
        private java.util.Date createdAt;
        private java.util.Date updatedAt;
        private Integer relatedObjects;
        private String reporter;
        private String cc;
        private Long productId;

        public FeedbackWithAssigneeName(Feedback feedback) {
            this.id = feedback.getId();
            this.title = feedback.getTitle();
            this.description = feedback.getDescription();
            this.priority = feedback.getPriority();
            this.status = feedback.getStatus();
            this.type = feedback.getType();
            this.assigneeId = feedback.getAssigneeId();
            this.solution = feedback.getSolution();
            this.creatorId = feedback.getCreatorId();
            this.createdAt = feedback.getCreatedAt();
            this.updatedAt = feedback.getUpdatedAt();
            this.relatedObjects = feedback.getRelatedObjects();
            this.reporter = feedback.getReporter();
            this.cc = feedback.getCc();
            this.productId = feedback.getProductId();
            this.assigneeName = "未指派";
            this.creatorName = "未知";
            this.teamId = null;
            this.teamName = "";
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getAssigneeId() { return assigneeId; }
        public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
        public String getAssigneeName() { return assigneeName; }
        public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
        public String getSolution() { return solution; }
        public void setSolution(String solution) { this.solution = solution; }
        public Long getCreatorId() { return creatorId; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
        public String getCreatorName() { return creatorName; }
        public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
        public Integer getTeamId() { return teamId; }
        public void setTeamId(Integer teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public java.util.Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
        public java.util.Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.util.Date updatedAt) { this.updatedAt = updatedAt; }
        public Integer getRelatedObjects() { return relatedObjects; }
        public void setRelatedObjects(Integer relatedObjects) { this.relatedObjects = relatedObjects; }
        public String getReporter() { return reporter; }
        public void setReporter(String reporter) { this.reporter = reporter; }
        public String getCc() { return cc; }
        public void setCc(String cc) { this.cc = cc; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
    }
}
