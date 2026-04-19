package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.Message;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.Task;
import com.example.springboot.entity.OperationLog;
import com.example.springboot.service.MessageService;
import com.example.springboot.service.TeamService;
import com.example.springboot.service.TeamMemberService;
import com.example.springboot.service.UserService;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.OperationLogService;
import com.example.springboot.repository.ProjectRepository;
import com.example.springboot.repository.TaskRepository;
import com.example.springboot.repository.TeamMemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/teams")
@Tag(name="团队管理模块")
public class TeamController {

    @Resource
    private MessageService messageService;
    
    @Resource
    private TeamService teamService;
    
    @Resource
    private TeamMemberService teamMemberService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private ProjectService projectService;
    
    @Resource
    private TaskService taskService;
    
    @Resource
    private ProjectRepository projectRepository;
    
    @Resource
    private TaskRepository taskRepository;
    
    @Resource
    private TeamMemberRepository teamMemberRepository;
    
    @Resource
    private OperationLogService operationLogService;



    // 获取消息
    @Operation(summary = "获取消息", description = "根据接收者获取消息列表")
    @GetMapping("/messages")
    public Result getMessages(@RequestParam String receiver) {
        try {
            List<Message> messages = messageService.findByReceiver(receiver);
            return Result.success(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取消息失败: " + e.getMessage());
        }
    }
    
    // 根据团队ID获取消息
    @Operation(summary = "根据团队ID获取消息", description = "根据团队ID获取消息列表")
    @GetMapping("/messages/by-team")
    @PostMapping("/messages/by-team")
    public Result getMessagesByTeam(@RequestParam(name = "teamId") Integer teamId, @RequestParam(name = "teamld", required = false) Integer teamld) {
        try {
            System.out.println("===== getMessagesByTeam 方法被调用 =====");
            System.out.println("teamId: " + teamId);
            System.out.println("teamld: " + teamld);
            // 使用teamId，如果teamld存在则使用teamld
            Integer finalTeamId = teamId != null ? teamId : (teamld != null ? teamld : 0);
            System.out.println("finalTeamId: " + finalTeamId);
            List<Message> messages = messageService.findByTeamId(finalTeamId);
            System.out.println("根据团队ID " + finalTeamId + " 获取到的消息数量: " + messages.size());
            // 打印每条消息的详细信息
            for (Message msg : messages) {
                System.out.println("消息ID: " + msg.getId() + ", 发送者: " + msg.getSender() + ", 接收者: " + msg.getReceiver() + ", 内容: " + msg.getContent() + ", 团队ID: " + msg.getTeamId());
            }
            return Result.success(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取消息失败: " + e.getMessage());
        }
    }
    
    // 测试GET请求
    @Operation(summary = "测试GET请求", description = "测试GET请求是否正常工作")
    @GetMapping("/test-get")
    public Result testGet() {
        System.out.println("===== testGet 方法被调用 =====");
        return Result.success("GET works");
    }

    // 获取未读消息
    @Operation(summary = "获取未读消息", description = "根据接收者获取未读消息列表")
    @GetMapping("/messages/unread")
    public Result getUnreadMessages(@RequestParam String receiver) {
        try {
            List<Message> messages = messageService.findUnreadByReceiver(receiver);
            return Result.success(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未读消息失败: " + e.getMessage());
        }
    }

    // 创建消息
    @Operation(summary = "创建消息", description = "创建新的消息")
    @PostMapping("/messages")
    public Result createMessage(@RequestBody Message message) {
        try {
            message.setCreatedAt(new Date());
            message.setIsRead(0); // 默认为未读
            Message savedMessage = messageService.save(message);
            System.out.println("创建消息成功: ID=" + savedMessage.getId() + ", 发送者=" + savedMessage.getSender() + ", 接收者=" + savedMessage.getReceiver() + ", 团队ID=" + savedMessage.getTeamId());
            return Result.success(savedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建消息失败: " + e.getMessage());
        }
    }

    // 标记消息为已读
    @Operation(summary = "标记消息为已读", description = "根据ID标记消息为已读")
    @PutMapping("/messages/{id}/read")
    public Result markMessageAsRead(@PathVariable Integer id) {
        try {
            messageService.markAsRead(id);
            return Result.success("标记消息为已读成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记消息为已读失败: " + e.getMessage());
        }
    }

    // 删除消息
    @Operation(summary = "删除消息", description = "根据ID删除消息")
    @DeleteMapping("/messages/{id}")
    public Result deleteMessage(@PathVariable Integer id) {
        try {
            messageService.deleteById(id);
            return Result.success("删除消息成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除消息失败: " + e.getMessage());
        }
    }
    
    // 获取团队概览
    @Operation(summary = "获取团队概览", description = "获取团队概览统计数据")
    @GetMapping("/overview")
    public Result getTeamOverview(@RequestParam String username) {
        try {
            System.out.println("===== getTeamOverview 方法被调用 ======");
            System.out.println("用户名: " + username);
            
            // 根据用户名获取用户信息
            User user = userService.findByUsername(username);
            if (user == null) {
                // 如果用户不存在，返回空数据
                System.out.println("用户不存在: " + username);
                Map<String, Object> overview = new HashMap<>();
                overview.put("totalTeams", 0);
                overview.put("totalMembers", 0);
                overview.put("totalProjects", 0);
                overview.put("totalMessages", 0);
                return Result.success(overview);
            }
            
            System.out.println("用户信息: ID=" + user.getId() + ", 姓名=" + user.getName());
            
            // 检查用户ID是否为null
            if (user.getId() == null) {
                // 如果用户ID为null，返回空数据
                System.out.println("用户ID为null");
                Map<String, Object> overview = new HashMap<>();
                overview.put("totalTeams", 0);
                overview.put("totalMembers", 0);
                overview.put("totalProjects", 0);
                overview.put("totalMessages", 0);
                return Result.success(overview);
            }
            
            // 使用用户ID获取团队成员记录
            List<TeamMember> teamMembers = new ArrayList<>();
            try {
                Long userIdLong = null;
                try {
                    // 使用用户名作为 team_members 表的 user_id
                    userIdLong = Long.parseLong(username);
                    System.out.println("使用用户名作为用户ID: " + userIdLong);
                } catch (NumberFormatException e) {
                    System.out.println("用户名不是数字格式: " + username);
                    userIdLong = 0L;
                }
                System.out.println("用户ID (Long): " + userIdLong);
                teamMembers = teamMemberService.findByUserId(userIdLong);
                System.out.println("获取到的团队成员记录数量: " + teamMembers.size());
            } catch (Exception e) {
                // 如果获取失败，返回空数据
                System.out.println("获取团队成员记录失败: " + e.getMessage());
                e.printStackTrace();
                Map<String, Object> overview = new HashMap<>();
                overview.put("totalTeams", 0);
                overview.put("totalMembers", 0);
                overview.put("totalProjects", 0);
                overview.put("totalMessages", 0);
                return Result.success(overview);
            }
            
            // 统计用户所在的团队数量
            long totalTeams = teamMembers.size();
            System.out.println("团队数量: " + totalTeams);
            
            // 使用Set去重统计总成员数（只统计与登录用户在同一团队的成员）
            Set<String> memberIds = new HashSet<>();
            // 统计与登录用户关联的项目（去重）
            Set<Long> projectIds = new HashSet<>();
            // 统计任务数量（使用Set去重，避免重复计数）
            Set<Integer> taskIds = new HashSet<>();
            
            // 遍历用户的团队成员记录
            for (TeamMember member : teamMembers) {
                System.out.println("处理团队成员: 团队ID=" + member.getTeamId() + ", 用户ID=" + member.getUserId());
                // 获取团队信息
                Optional<Team> teamOpt = teamService.findById(member.getTeamId());
                if (teamOpt.isPresent()) {
                    Team team = teamOpt.get();
                    System.out.println("团队信息: ID=" + team.getId() + ", 名称=" + team.getName());
                    // 获取团队成员列表
                    List<TeamMember> teamMembersList = teamMemberService.findByTeamId(member.getTeamId());
                    System.out.println("团队成员数量: " + teamMembersList.size());
                    // 去重添加成员ID（使用字符串形式存储）
                    for (TeamMember teamMember : teamMembersList) {
                        // 确保只添加有效的用户ID
                        if (teamMember.getUserId() != null) {
                            memberIds.add(teamMember.getUserId().toString());
                            
                            // 查找该成员管理的项目
                            try {
                                List<Project> memberProjects = projectRepository.findByManagerId(teamMember.getUserId());
                                System.out.println("成员管理的项目数量: " + memberProjects.size());
                                for (Project project : memberProjects) {
                                    projectIds.add(project.getId());
                                    // 查找每个项目的任务并添加到Set中去重
                                    try {
                                        int projectIdInt = Math.toIntExact(project.getId());
                                        List<Task> projectTasks = taskRepository.findByProjectId(projectIdInt);
                                        System.out.println("项目任务数量: " + projectTasks.size());
                                        for (Task task : projectTasks) {
                                            taskIds.add(task.getId());
                                        }
                                    } catch (Exception e) {
                                        System.out.println("处理项目任务失败: " + e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("查找成员管理的项目失败: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            
            // 计算任务数量（去重后）
            int totalTasks = taskIds.size();
            System.out.println("任务数量: " + totalTasks);
            
            // 计算去重后的总成员数
            long totalMembers = memberIds.size();
            System.out.println("成员数量: " + totalMembers);
            // 计算去重后的项目数量
            long totalProjects = projectIds.size();
            System.out.println("项目数量: " + totalProjects);
            
            // 统计消息数量：统计登录用户所属团队的所有消息数量
            long totalMessages = 0;
            try {
                // 首先获取用户所属的所有团队
                Long userIdLong = null;
                try {
                    // 使用用户名作为 team_members 表的 user_id
                    userIdLong = Long.parseLong(username);
                    System.out.println("使用用户名作为用户ID: " + userIdLong);
                } catch (NumberFormatException e) {
                    System.out.println("用户名不是数字格式: " + username);
                    userIdLong = 0L;
                }
                List<TeamMember> userTeams = teamMemberService.findByUserId(userIdLong);
                
                // 收集所有团队ID
                Set<Integer> teamIds = new HashSet<>();
                for (TeamMember teamMember : userTeams) {
                    teamIds.add(teamMember.getTeamId());
                }
                System.out.println("用户所属团队ID列表: " + teamIds);
                
                // 统计与这些团队相关的消息数量
                if (!teamIds.isEmpty()) {
                    // 直接统计这些团队的消息数量
                    totalMessages = 0;
                    for (Integer teamId : teamIds) {
                        List<Message> teamMessages = messageService.findByTeamId(teamId);
                        totalMessages += teamMessages.size();
                    }
                    System.out.println("统计到的团队消息数量: " + totalMessages);
                }
                
                // 同时统计接收者为当前用户的消息数量，确保不遗漏
                long userMessages = messageService.countByReceiver(username);
                totalMessages += userMessages;
                System.out.println("统计到的用户消息数量: " + userMessages);
                System.out.println("总消息数量: " + totalMessages);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("统计消息数量失败: " + e.getMessage());
                totalMessages = 0;
            }
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalTeams", totalTeams);
            overview.put("totalMembers", totalMembers);
            overview.put("totalProjects", totalProjects);
            overview.put("totalMessages", totalMessages);
            
            System.out.println("返回团队概览数据: " + overview);
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取团队概览失败: " + e.getMessage());
            return Result.error("获取团队概览失败: " + e.getMessage());
        }
    }
    
    // 获取当前用户的所属团队
    @Operation(summary = "获取所属团队", description = "获取当前用户的所属团队列表")
    @GetMapping("/my-teams")
    public Result getMyTeams(@RequestParam String username) {
        try {
            // 根据用户名获取用户ID
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 构建团队列表
            List<Map<String, Object>> teams = new ArrayList<>();
            
            // 获取用户的团队成员记录
            Long userIdLong = null;
            try {
                // 使用用户名作为 team_members 表的 user_id
                userIdLong = Long.parseLong(username);
                System.out.println("使用用户名作为用户ID: " + userIdLong);
            } catch (NumberFormatException e) {
                System.out.println("用户名不是数字格式: " + username);
                userIdLong = 0L;
            }
            List<TeamMember> teamMembers = teamMemberService.findByUserId(userIdLong);
            for (TeamMember member : teamMembers) {
                Optional<Team> teamOpt = teamService.findById(member.getTeamId());
                if (teamOpt.isPresent()) {
                    Team team = teamOpt.get();
                    Map<String, Object> teamMap = new HashMap<>();
                    teamMap.put("id", team.getId());
                    teamMap.put("name", team.getName());
                    teamMap.put("description", team.getDescription());
                    teamMap.put("createdAt", team.getCreatedAt());
                    
                    // 获取团队成员详情
                    List<TeamMember> teamMembersList = teamMemberService.findByTeamId(team.getId());
                    List<Map<String, Object>> memberDetails = new ArrayList<>();
                    for (TeamMember teamMember : teamMembersList) {
                        User memberUser = userService.findByUsername(teamMember.getUserId().toString());
                        if (memberUser != null) {
                            Map<String, Object> memberMap = new HashMap<>();
                            memberMap.put("userId", teamMember.getUserId());
                            memberMap.put("name", memberUser.getName());
                            memberMap.put("role", teamMember.getRoleInTeam());
                            memberMap.put("username", memberUser.getUsername());
                            memberDetails.add(memberMap);
                        }
                    }
                    teamMap.put("memberDetails", memberDetails);
                    
                    // 获取团队项目数量（真实数据）
                    List<Long> teamProjectIds = new ArrayList<>();
                    // 获取团队所有成员的ID
                    Set<Long> teamMemberIds = new HashSet<>();
                    System.out.println("团队ID: " + team.getId() + "，团队名称: " + team.getName() + "，团队成员数量: " + teamMembersList.size());
                    for (TeamMember teamMember : teamMembersList) {
                        teamMemberIds.add(teamMember.getUserId());
                        System.out.println("团队成员ID: " + teamMember.getUserId() + "，角色: " + teamMember.getRoleInTeam());
                    }
                    
                    // 查找所有由团队成员管理的项目
                    List<Project> allProjects = projectRepository.findAll();
                    System.out.println("数据库中总项目数量: " + allProjects.size());
                    for (Project project : allProjects) {
                        System.out.println("项目ID: " + project.getId() + "，项目名称: " + project.getName() + "，管理者ID: " + project.getManagerId());
                        if (project.getManagerId() != null && teamMemberIds.contains(project.getManagerId())) {
                            teamProjectIds.add(project.getId());
                            System.out.println("项目ID: " + project.getId() + " 属于当前团队");
                        }
                    }
                    
                    long projectCount = teamProjectIds.size();
                    teamMap.put("projects", projectCount);
                    System.out.println("团队ID: " + team.getId() + "，团队名称: " + team.getName() + "，项目数量: " + projectCount);
                    
                    // 获取团队任务数量（真实数据，不进行去重，只统计团队项目的任务）
                    int teamTaskCount = 0;
                    System.out.println("开始统计团队任务数量，团队ID: " + team.getId() + "，团队名称: " + team.getName());
                    
                    // 首先获取团队所有项目的ID
                    Set<Integer> teamProjectIdInts = new HashSet<>();
                    for (Long projectId : teamProjectIds) {
                        teamProjectIdInts.add(Math.toIntExact(projectId));
                    }
                    System.out.println("团队项目ID列表: " + teamProjectIdInts);
                    
                    // 查找所有属于团队项目的任务
                    List<Task> allTasks = taskRepository.findAll();
                    System.out.println("数据库中总任务数量: " + allTasks.size());
                    for (Task task : allTasks) {
                        System.out.println("任务ID: " + task.getId() + "，项目ID: " + task.getProjectId());
                        if (task.getProjectId() != null && teamProjectIdInts.contains(task.getProjectId())) {
                            teamTaskCount++;
                            System.out.println("任务ID: " + task.getId() + " 属于当前团队项目");
                        }
                    }
                    
                    System.out.println("团队ID: " + team.getId() + "，团队名称: " + team.getName() + "，任务数量: " + teamTaskCount);
                    teamMap.put("tasks", teamTaskCount);
                    
                    teams.add(teamMap);
                }
            }
            
            return Result.success(teams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取所属团队失败: " + e.getMessage());
        }
    }
    
    // 创建团队
    @Operation(summary = "创建团队", description = "创建新的团队")
    @PostMapping("/create")
    @Transactional
    public Result createTeam(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String description = (String) body.get("description");
            Object usernameObj = body.get("username");
            String username = null;
            
            // 处理不同类型的username参数
            if (usernameObj instanceof String) {
                username = (String) usernameObj;
            } else if (usernameObj instanceof Integer) {
                username = usernameObj.toString();
            } else if (usernameObj instanceof Long) {
                username = usernameObj.toString();
            }
            
            // 验证参数
            if (name == null || name.isEmpty()) {
                return Result.error("团队名称不能为空");
            }
            
            if (username == null) {
                return Result.error("用户名不能为空");
            }
            
            // 获取用户信息
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 创建团队
            Team team = new Team();
            team.setName(name);
            team.setDescription(description);
            // 使用用户名为创建者ID
            team.setCreatorId(Long.parseLong(username));
            team.setCreatedAt(new Date());
            team.setUpdatedAt(new Date());
            Team savedTeam = teamService.save(team);
            
            // 添加创建者为团队成员
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(savedTeam.getId());
            // 使用用户名为成员ID
            teamMember.setUserId(Long.parseLong(username));
            teamMember.setRoleInTeam("1"); // 团队负责人
            teamMember.setJoinedAt(new Date());
            teamMemberService.save(teamMember);
            
            // 记录操作日志
            OperationLog operationLog = new OperationLog();
            operationLog.setUser_id(username);
            operationLog.setAction("创建团队: " + name);
            operationLog.setModule("团队管理");
            operationLog.setTarget_id(savedTeam.getId().longValue());
            operationLog.setCreated_at(new Date());
            operationLogService.save(operationLog);
            System.out.println("记录操作日志成功: 操作人=" + username + "，团队ID=" + savedTeam.getId());
            
            return Result.success(savedTeam);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建团队失败: " + e.getMessage());
        }
    }
    
    // 邀请成员
    @Operation(summary = "邀请成员", description = "邀请成员加入团队")
    @PostMapping("/invite")
    @Transactional
    public Result inviteMember(@RequestBody Map<String, Object> body) {
        try {
            String teamIdStr = (String) body.get("teamId");
            Object usernameObj = body.get("username");
            String role = (String) body.get("role");
            String username = null;
            String operator = null;
            
            // 处理不同类型的username参数
            if (usernameObj instanceof String) {
                username = (String) usernameObj;
            } else if (usernameObj instanceof Integer) {
                username = usernameObj.toString();
            } else if (usernameObj instanceof Long) {
                username = usernameObj.toString();
            }
            
            // 处理不同类型的operator参数
            Object operatorObj = body.get("operator");
            if (operatorObj instanceof String) {
                operator = (String) operatorObj;
            } else if (operatorObj instanceof Integer) {
                operator = operatorObj.toString();
            } else if (operatorObj instanceof Long) {
                operator = operatorObj.toString();
            }
            
            // 验证参数
            if (teamIdStr == null || username == null || role == null || operator == null) {
                return Result.error("参数不能为空");
            }
            
            // 提取工号部分，处理"王五 (202203)"这样的格式
            String userIdStr = username;
            if (username.contains("(")) {
                int startIndex = username.indexOf("(");
                int endIndex = username.indexOf(")");
                if (startIndex > 0 && endIndex > startIndex) {
                    userIdStr = username.substring(startIndex + 1, endIndex);
                }
            }
            
            // 获取用户信息
            User user = userService.findByUsername(userIdStr);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 查找团队（支持通过团队ID或团队名称查找）
            Team targetTeam = null;
            try {
                // 尝试通过ID查找团队
                Integer teamId = Integer.parseInt(teamIdStr);
                Optional<Team> teamOpt = teamService.findById(teamId);
                if (teamOpt.isPresent()) {
                    targetTeam = teamOpt.get();
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，则通过名称查找团队
                List<Team> teams = teamService.findAll();
                for (Team team : teams) {
                    if (team.getName().equals(teamIdStr)) {
                        targetTeam = team;
                        break;
                    }
                }
            }
            
            if (targetTeam == null) {
                return Result.error("团队不存在");
            }
            
            // 不再检查用户是否已经是团队成员，允许重复邀请
            System.out.println("邀请成员: 团队ID=" + targetTeam.getId() + ", 用户名=" + username + ", 用户ID=" + userIdStr + ", 操作人=" + operator);
            
            // 将角色名称转换为角色ID
            String roleId = "3"; // 默认开发
            if ("项目经理".equals(role)) {
                roleId = "2";
            } else if ("测试工程师".equals(role)) {
                roleId = "4";
            }
            
            // 添加成员到团队
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(targetTeam.getId());
            teamMember.setUserId(Long.parseLong(userIdStr));
            teamMember.setRoleInTeam(roleId);
            teamMember.setJoinedAt(new Date());
            // 设置邀请人ID
            try {
                teamMember.setJoinedBy(Long.parseLong(operator));
            } catch (NumberFormatException e) {
                // 如果operator不是数字格式，忽略
            }
            teamMemberService.save(teamMember);
            
            // 创建邀请消息
            Message message = new Message();
            message.setSender("系统");
            message.setReceiver(userIdStr);
            message.setContent("您已被邀请加入" + targetTeam.getName() + "团队，角色为" + role);
            message.setTeamId(targetTeam.getId()); // 设置团队ID
            message.setCreatedAt(new Date());
            message.setIsRead(0);
            messageService.save(message);
            System.out.println("创建邀请消息成功: 团队ID=" + targetTeam.getId() + ", 接收者=" + userIdStr);
            
            // 记录操作日志
            OperationLog operationLog = new OperationLog();
            operationLog.setUser_id(operator);
            operationLog.setAction("邀请成员加入团队: " + targetTeam.getName() + "，成员: " + username + "，角色: " + role);
            operationLog.setModule("团队管理");
            operationLog.setTarget_id(targetTeam.getId().longValue());
            operationLog.setCreated_at(new Date());
            operationLogService.save(operationLog);
            System.out.println("记录操作日志成功: 操作人=" + operator + "，团队ID=" + targetTeam.getId());
            
            return Result.success("邀请成员成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("邀请成员失败: " + e.getMessage());
        }
    }
    
    // 编辑分工
    @Operation(summary = "编辑分工", description = "编辑团队成员分工")
    @PutMapping("/division/edit")
    @Transactional
    public Result editDivision(@RequestBody Map<String, Object> body) {
        try {
            Integer teamId = (Integer) body.get("teamId");
            Long userId = Long.parseLong((String) body.get("userId"));
            String role = (String) body.get("role");
            
            // 验证参数
            if (teamId == null || userId == null || role == null) {
                return Result.error("参数不能为空");
            }
            
            // 查找团队成员
            TeamMember teamMember = teamMemberService.findByTeamIdAndUserId(teamId, userId);
            if (teamMember == null) {
                return Result.error("团队成员不存在");
            }
            
            // 将角色名称转换为角色ID
            String roleId = "3"; // 默认开发
            if ("项目经理".equals(role)) {
                roleId = "2";
            } else if ("测试工程师".equals(role)) {
                roleId = "4";
            }
            
            // 更新角色
            teamMember.setRoleInTeam(roleId);
            teamMemberService.save(teamMember);
            
            return Result.success("分工编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("编辑分工失败: " + e.getMessage());
        }
    }
    
    // 标记消息为已读
    @Operation(summary = "标记消息为已读", description = "标记消息为已读")
    @PutMapping("/messages/read")
    public Result markMessageAsRead(@RequestBody Map<String, Object> body) {
        try {
            Integer messageId = (Integer) body.get("messageId");
            
            // 验证参数
            if (messageId == null) {
                return Result.error("消息ID不能为空");
            }
            
            // 标记为已读
            messageService.markAsRead(messageId);
            
            return Result.success("标记消息为已读成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记消息为已读失败: " + e.getMessage());
        }
    }
    
    // 查看数据库中的任务数据
    @Operation(summary = "查看任务数据", description = "查看数据库中的任务数据")
    @GetMapping("/tasks")
    public Result getTasks() {
        try {
            List<Task> tasks = taskRepository.findAll();
            System.out.println("数据库中的任务数量: " + tasks.size());
            for (Task task : tasks) {
                System.out.println("任务ID: " + task.getId() + ", 项目ID: " + task.getProjectId() + ", 负责人ID: " + task.getAssigneeId() + ", 创建者ID: " + task.getCreatorId());
            }
            return Result.success(tasks);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取任务数据失败: " + e.getMessage());
        }
    }
    
    // 查看数据库中的统计数据
    @Operation(summary = "查看统计数据", description = "查看数据库中的团队、成员、项目和任务的数量")
    @GetMapping("/statistics")
    public Result getDatabaseStatistics() {
        try {
            // 统计团队数量
            long teamCount = teamService.count();
            System.out.println("数据库中的团队数量: " + teamCount);
            
            // 统计成员数量
            long memberCount = teamMemberService.count();
            System.out.println("数据库中的团队成员数量: " + memberCount);
            
            // 统计项目数量
            long projectCount = projectService.count();
            System.out.println("数据库中的项目数量: " + projectCount);
            
            // 统计任务数量
            long taskCount = taskRepository.count();
            System.out.println("数据库中的任务数量: " + taskCount);
            
            // 构建响应数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("teamCount", teamCount);
            statistics.put("memberCount", memberCount);
            statistics.put("projectCount", projectCount);
            statistics.put("taskCount", taskCount);
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    // 获取个人任务统计数据
    @Operation(summary = "获取个人任务统计", description = "获取用户的任务数量和逾期任务数量")
    @GetMapping("/personal-tasks")
    public Result getPersonalTaskStatistics(@RequestParam String username) {
        try {
            // 获取用户的所有任务
            List<Task> userTasks = taskRepository.findByAssigneeId(Integer.parseInt(username));
            
            // 计算当前任务数量（未完成的任务）
            long currentTasks = userTasks.stream()
                .filter(task -> task.getStatus() != null && task.getStatus() != 3) // 假设3代表已完成状态
                .count();
            
            // 计算逾期任务数量（未完成且截止日期已过的任务）
            long overdueTasks = 0;
            String today = java.time.LocalDate.now().toString();
            for (Task task : userTasks) {
                if (task.getStatus() != null && task.getStatus() != 3 && task.getDueDate() != null) {
                    try {
                        java.time.LocalDate taskDueDate = java.time.LocalDate.parse(task.getDueDate());
                        java.time.LocalDate todayDate = java.time.LocalDate.parse(today);
                        if (taskDueDate.isBefore(todayDate)) {
                            overdueTasks++;
                        }
                    } catch (Exception e) {
                        // 日期解析失败，跳过
                    }
                }
            }
            
            // 计算任务完成率
            long completedTasks = userTasks.stream()
                .filter(task -> task.getStatus() != null && task.getStatus() == 3) // 假设3代表已完成状态
                .count();
            int completionRate = userTasks.isEmpty() ? 0 : (int) (completedTasks * 100 / userTasks.size());
            
            // 构建响应数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("currentTasks", currentTasks);
            statistics.put("overdueTasks", overdueTasks);
            statistics.put("completionRate", completionRate);
            statistics.put("totalTasks", userTasks.size());
            
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取个人任务统计失败: " + e.getMessage());
        }
    }
    
    // 测试方法：获取所有消息
    @Operation(summary = "获取所有消息", description = "获取数据库中的所有消息")
    @GetMapping("/messages/all")
    public Result getAllMessages() {
        try {
            List<Message> messages = messageService.findAll();
            System.out.println("数据库中的消息数量: " + messages.size());
            for (Message msg : messages) {
                System.out.println("消息ID: " + msg.getId() + ", 发送者: " + msg.getSender() + ", 接收者: " + msg.getReceiver() + ", 内容: " + msg.getContent());
            }
            return Result.success(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取所有消息失败: " + e.getMessage());
        }
    }
    
    // 测试方法：获取所有团队成员
    @Operation(summary = "获取所有团队成员", description = "获取数据库中的所有团队成员")
    @GetMapping("/members/all")
    public Result getAllTeamMembers() {
        try {
            List<TeamMember> members = teamMemberRepository.findAll();
            System.out.println("数据库中的团队成员数量: " + members.size());
            for (TeamMember member : members) {
                System.out.println("团队成员ID: " + member.getId() + ", 团队ID: " + member.getTeamId() + ", 用户ID: " + member.getUserId() + ", 角色: " + member.getRoleInTeam());
            }
            return Result.success(members);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取所有团队成员失败: " + e.getMessage());
        }
    }
    
    // 解散团队
    @Operation(summary = "解散团队", description = "解散团队并删除相关数据")
    @DeleteMapping("/disband/{teamId}")
    @Transactional
    public Result disbandTeam(@PathVariable Integer teamId) {
        try {
            // 验证团队是否存在
            Optional<Team> teamOpt = teamService.findById(teamId);
            if (!teamOpt.isPresent()) {
                return Result.error("团队不存在");
            }
            
            Team team = teamOpt.get();
            
            // 删除团队成员
            List<TeamMember> teamMembers = teamMemberService.findByTeamId(teamId);
            for (TeamMember member : teamMembers) {
                teamMemberService.deleteById(member.getId());
            }
            
            // 删除团队相关消息
            List<Message> messages = messageService.findByTeamId(teamId);
            for (Message message : messages) {
                messageService.deleteById(message.getId());
            }
            
            // 删除团队
            teamService.deleteById(team.getId());
            
            return Result.success("团队解散成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("解散团队失败: " + e.getMessage());
        }
    }
    
    // 删除团队成员
    @Operation(summary = "删除团队成员", description = "从团队中删除成员")
    @DeleteMapping("/remove-member")
    @Transactional
    public Result removeMember(@RequestBody Map<String, Object> body) {
        try {
            Integer teamId = (Integer) body.get("teamId");
            Long userId = Long.parseLong(body.get("userId").toString());
            
            // 验证参数
            if (teamId == null || userId == null) {
                return Result.error("参数不能为空");
            }
            
            // 查找团队成员
            TeamMember teamMember = teamMemberService.findByTeamIdAndUserId(teamId, userId);
            if (teamMember == null) {
                return Result.error("团队成员不存在");
            }
            
            // 删除团队成员
            teamMemberService.deleteById(teamMember.getId());
            
            return Result.success("成员删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除成员失败: " + e.getMessage());
        }
    }
}