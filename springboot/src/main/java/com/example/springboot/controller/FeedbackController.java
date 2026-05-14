package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Feedback;
import com.example.springboot.entity.ProjectApproval;
import com.example.springboot.entity.User;
import com.example.springboot.service.FeedbackService;
import com.example.springboot.service.ProjectApprovalService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private ProjectApprovalService projectApprovalService;
    
    @Autowired
    private UserService userService;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 获取反馈列表
    @GetMapping("/list")
    public Result getFeedbackList() {
        try {
            List<FeedbackService.FeedbackWithAssigneeName> feedbacks = feedbackService.findAll();
            return Result.success(feedbacks);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取反馈列表失败: " + e.getMessage());
        }
    }

    // 创建反馈
    @PostMapping("/create")
    public Result createFeedback(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("创建反馈请求: " + requestBody);
            
            Feedback feedback = new Feedback();
            
            // 提取字段
            if (requestBody.containsKey("title")) {
                feedback.setTitle((String) requestBody.get("title"));
            }
            if (requestBody.containsKey("description")) {
                feedback.setDescription((String) requestBody.get("description"));
            }
            if (requestBody.containsKey("type")) {
                feedback.setType((String) requestBody.get("type"));
            }
            if (requestBody.containsKey("reporter")) {
                feedback.setReporter((String) requestBody.get("reporter"));
            }
            if (requestBody.containsKey("productId")) {
                Object productIdObj = requestBody.get("productId");
                if (productIdObj instanceof Integer) {
                    feedback.setProductId(((Integer) productIdObj).longValue());
                } else if (productIdObj instanceof Long) {
                    feedback.setProductId((Long) productIdObj);
                } else if (productIdObj instanceof String) {
                    try {
                        feedback.setProductId(Long.parseLong((String) productIdObj));
                    } catch (NumberFormatException e) {
                        // 忽略
                    }
                }
            }
            
            // 设置创建人ID
            if (requestBody.containsKey("creatorId")) {
                Object creatorIdObj = requestBody.get("creatorId");
                Long creatorId = null;
                if (creatorIdObj instanceof Integer) {
                    creatorId = ((Integer) creatorIdObj).longValue();
                } else if (creatorIdObj instanceof Long) {
                    creatorId = (Long) creatorIdObj;
                } else if (creatorIdObj instanceof String) {
                    try {
                        creatorId = Long.parseLong((String) creatorIdObj);
                    } catch (NumberFormatException e) {
                        // 尝试通过用户名查找用户
                        User user = userService.findByUsername((String) creatorIdObj);
                        if (user != null) {
                            try {
                                creatorId = Long.parseLong(user.getUsername());
                            } catch (NumberFormatException ex) {
                                // 忽略
                            }
                        }
                    }
                }
                feedback.setCreatorId(creatorId);
                System.out.println("设置创建人ID: " + creatorId);
            }
            
            // 设置指派人ID
            if (requestBody.containsKey("assigneeId")) {
                Object assigneeIdObj = requestBody.get("assigneeId");
                Long assigneeId = null;
                if (assigneeIdObj instanceof Integer) {
                    assigneeId = ((Integer) assigneeIdObj).longValue();
                } else if (assigneeIdObj instanceof Long) {
                    assigneeId = (Long) assigneeIdObj;
                } else if (assigneeIdObj instanceof String) {
                    try {
                        assigneeId = Long.parseLong((String) assigneeIdObj);
                    } catch (NumberFormatException e) {
                        // 尝试通过用户名查找用户
                        User user = userService.findByUsername((String) assigneeIdObj);
                        if (user != null) {
                            try {
                                assigneeId = Long.parseLong(user.getUsername());
                            } catch (NumberFormatException ex) {
                                // 忽略
                            }
                        }
                    }
                }
                feedback.setAssigneeId(assigneeId);
                System.out.println("设置指派人ID: " + assigneeId);
            }
            
            // 设置默认状态
            if (feedback.getStatus() == null || feedback.getStatus().isEmpty()) {
                feedback.setStatus("待处理");
            }
            if (feedback.getCreatedAt() == null) {
                feedback.setCreatedAt(new Date());
            }
            if (feedback.getUpdatedAt() == null) {
                feedback.setUpdatedAt(new Date());
            }
            
            Feedback savedFeedback = feedbackService.save(feedback);
            System.out.println("保存反馈成功: " + savedFeedback.getId());
            
            // 如果类型是功能建议，同步创建项目待审批记录
            if ("suggestion".equals(feedback.getType())) {
                ProjectApproval approval = new ProjectApproval();
                approval.setType("研发需求");
                approval.setAction("待审批");
                approval.setComment("功能建议: " + feedback.getTitle());
                approval.setCreated_at(sdf.format(new Date()));
                
                // 反馈的productId对应项目id
                if (feedback.getProductId() != null) {
                    approval.setProject_id(feedback.getProductId().intValue());
                }
                
                // 设置审批人为指派人，如果没有指派人则为创建人
                if (feedback.getAssigneeId() != null) {
                    approval.setApprover_id(feedback.getAssigneeId().intValue());
                    System.out.println("设置审批人为指派人: " + feedback.getAssigneeId());
                } else if (feedback.getCreatorId() != null) {
                    approval.setApprover_id(feedback.getCreatorId().intValue());
                    System.out.println("设置审批人为创建人: " + feedback.getCreatorId());
                }
                
                // 保存反馈ID，用于后续审批状态同步
                approval.setFeedback_id(savedFeedback.getId());
                
                projectApprovalService.save(approval);
                System.out.println("创建审批记录成功");
            }
            
            return Result.success(savedFeedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建反馈失败: " + e.getMessage());
        }
    }

    // 更新反馈
    @PutMapping("/update")
    public Result updateFeedback(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("更新反馈请求: " + requestBody);
            
            // 获取反馈ID
            Object idObj = requestBody.get("id");
            Long feedbackId = null;
            if (idObj instanceof Integer) {
                feedbackId = ((Integer) idObj).longValue();
            } else if (idObj instanceof Long) {
                feedbackId = (Long) idObj;
            } else if (idObj instanceof String) {
                feedbackId = Long.parseLong((String) idObj);
            }
            
            if (feedbackId == null) {
                return Result.error("反馈ID不能为空");
            }
            
            // 查找现有反馈
            Feedback existingFeedback = feedbackService.findEntityById(feedbackId);
            if (existingFeedback == null) {
                return Result.error("反馈不存在");
            }
            
            // 更新字段
            if (requestBody.containsKey("title")) {
                existingFeedback.setTitle((String) requestBody.get("title"));
            }
            if (requestBody.containsKey("description")) {
                existingFeedback.setDescription((String) requestBody.get("description"));
            }
            if (requestBody.containsKey("type")) {
                existingFeedback.setType((String) requestBody.get("type"));
            }
            if (requestBody.containsKey("status")) {
                existingFeedback.setStatus((String) requestBody.get("status"));
            }
            if (requestBody.containsKey("reporter")) {
                existingFeedback.setReporter((String) requestBody.get("reporter"));
            }
            if (requestBody.containsKey("productId")) {
                Object productIdObj = requestBody.get("productId");
                if (productIdObj instanceof Integer) {
                    existingFeedback.setProductId(((Integer) productIdObj).longValue());
                } else if (productIdObj instanceof Long) {
                    existingFeedback.setProductId((Long) productIdObj);
                } else if (productIdObj instanceof String) {
                    existingFeedback.setProductId(Long.parseLong((String) productIdObj));
                }
            }
            
            // 更新指派人ID
            if (requestBody.containsKey("assigneeId")) {
                Object assigneeIdObj = requestBody.get("assigneeId");
                Long assigneeId = null;
                if (assigneeIdObj instanceof Integer) {
                    assigneeId = ((Integer) assigneeIdObj).longValue();
                } else if (assigneeIdObj instanceof Long) {
                    assigneeId = (Long) assigneeIdObj;
                } else if (assigneeIdObj instanceof String) {
                    try {
                        assigneeId = Long.parseLong((String) assigneeIdObj);
                    } catch (NumberFormatException e) {
                        // 尝试通过用户名查找用户
                        User user = userService.findByUsername((String) assigneeIdObj);
                        if (user != null) {
                            try {
                                assigneeId = Long.parseLong(user.getUsername());
                            } catch (NumberFormatException ex) {
                                // 忽略
                            }
                        }
                    }
                }
                existingFeedback.setAssigneeId(assigneeId);
                System.out.println("更新指派人ID: " + assigneeId);
            }
            
            existingFeedback.setUpdatedAt(new Date());
            Feedback updatedFeedback = feedbackService.save(existingFeedback);
            System.out.println("更新反馈成功");
            
            // 如果是功能建议类型，同步更新项目待审批记录的相关信息
            if ("suggestion".equals(existingFeedback.getType())) {
                // 查找关联的审批记录
                ProjectApproval approval = projectApprovalService.findByFeedbackId(existingFeedback.getId());
                if (approval != null) {
                    // 同步更新审批记录的相关信息
                    if (existingFeedback.getTitle() != null) {
                        approval.setComment("功能建议: " + existingFeedback.getTitle());
                    }
                    
                    // 同步更新项目ID
                    if (existingFeedback.getProductId() != null) {
                        approval.setProject_id(existingFeedback.getProductId().intValue());
                    }
                    
                    // 同步更新审批人
                    if (existingFeedback.getAssigneeId() != null) {
                        approval.setApprover_id(existingFeedback.getAssigneeId().intValue());
                    } else if (existingFeedback.getCreatorId() != null) {
                        approval.setApprover_id(existingFeedback.getCreatorId().intValue());
                    }
                    
                    projectApprovalService.save(approval);
                    System.out.println("同步更新审批记录成功");
                }
            }
            
            return Result.success(updatedFeedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新反馈失败: " + e.getMessage());
        }
    }

    // 删除反馈
    @DeleteMapping("/delete/{id}")
    public Result deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除反馈失败: " + e.getMessage());
        }
    }

    // 获取单个反馈
    @GetMapping("/detail/{id}")
    public Result getFeedbackDetail(@PathVariable Long id) {
        try {
            FeedbackService.FeedbackWithAssigneeName feedback = feedbackService.findById(id);
            return Result.success(feedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取反馈详情失败: " + e.getMessage());
        }
    }
}
