package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamAnnouncement;
import com.example.springboot.entity.Message;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.User;
import com.example.springboot.service.TeamAnnouncementService;
import com.example.springboot.service.MessageService;
import com.example.springboot.service.TeamService;
import com.example.springboot.service.TeamMemberService;
import com.example.springboot.service.UserService;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;

@RestController
@RequestMapping("/team")
@Tag(name="团队管理模块")
public class TeamController {

    @Resource
    private TeamAnnouncementService teamAnnouncementService;

    @Resource
    private MessageService messageService;
    
    @Resource
    private TeamService teamService;
    
    @Resource
    private TeamMemberService teamMemberService;
    
    @Resource
    private UserService userService;

    // 获取团队公告
    @Operation(summary = "获取团队公告", description = "根据团队ID获取公告列表")
    @GetMapping("/announcements")
    public Result getAnnouncements(@RequestParam Integer teamId) {
        try {
            List<TeamAnnouncement> announcements = teamAnnouncementService.findByTeamId(teamId);
            return Result.success(announcements);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取公告失败: " + e.getMessage());
        }
    }

    // 创建公告
    @Operation(summary = "创建公告", description = "创建新的团队公告")
    @PostMapping("/announcements")
    public Result createAnnouncement(@RequestBody TeamAnnouncement announcement) {
        try {
            announcement.setCreatedAt(new Date());
            TeamAnnouncement savedAnnouncement = teamAnnouncementService.save(announcement);
            return Result.success(savedAnnouncement);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建公告失败: " + e.getMessage());
        }
    }

    // 删除公告
    @Operation(summary = "删除公告", description = "根据ID删除公告")
    @DeleteMapping("/announcements/{id}")
    public Result deleteAnnouncement(@PathVariable Integer id) {
        try {
            teamAnnouncementService.deleteById(id);
            return Result.success("删除公告成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除公告失败: " + e.getMessage());
        }
    }

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
            // 计算团队数量
            long totalTeams = teamService.count();
            
            // 计算成员数量
            long totalMembers = teamMemberService.count();
            
            // 计算项目数量（这里简化处理，实际应该从项目表中获取）
            long totalProjects = 0;
            
            // 计算任务数量（这里简化处理，实际应该从任务表中获取）
            long totalTasks = 0;
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalTeams", totalTeams);
            overview.put("totalMembers", totalMembers);
            overview.put("totalProjects", totalProjects);
            overview.put("totalTasks", totalTasks);
            
            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
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
            List<TeamMember> teamMembers = new ArrayList<>();
            try {
                teamMembers = teamMemberService.findByUserId(Long.parseLong(username));
            } catch (NumberFormatException e) {
                // 如果用户名不是数字格式，返回空列表
                return Result.success(teams);
            }
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
    public Result createTeam(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            String description = (String) body.get("description");
            String username = (String) body.get("username");
            
            // 验证参数
            if (name == null || name.isEmpty()) {
                return Result.error("团队名称不能为空");
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
            team.setCreatorId(Long.parseLong(username));
            team.setCreatedAt(new Date());
            team.setUpdatedAt(new Date());
            Team savedTeam = teamService.save(team);
            
            // 添加创建者为团队成员
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(savedTeam.getId());
            teamMember.setUserId(Long.parseLong(username));
            teamMember.setRoleInTeam("团队负责人");
            teamMember.setJoinedAt(new Date());
            teamMemberService.save(teamMember);
            
            return Result.success(savedTeam);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建团队失败: " + e.getMessage());
        }
    }
}