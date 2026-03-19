package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.Task;
import com.example.springboot.entity.User;
import com.example.springboot.service.TeamService;
import com.example.springboot.service.TeamMemberService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@Tag(name = "团队模块")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private TeamMemberService teamMemberService;

    @Resource
    private TaskService taskService;

    /**
     * 团队概览统计：只统计当前登录用户所在团队
     */
    @Operation(summary = "获取团队概览统计", description = "返回团队数量、成员数量、项目数量、任务数量，仅包含当前用户所在团队")
    @GetMapping("/overview")
    public Result getOverview(@RequestParam String username) {
        try {
            Optional<User> userOpt = userService.findById(username);
            if (userOpt.isEmpty()) {
                return Result.error("用户不存在");
            }

            // 与 WorkbenchController 保持一致，直接使用 username 转换为 Long 来获取团队数据
            Long userId = Long.parseLong(userOpt.get().getUsername());
            List<Team> userTeams = teamService.findTeamsByUserId(userId);

            if (userTeams.isEmpty()) {
                Map<String, Integer> empty = new HashMap<>();
                empty.put("totalTeams", 0);
                empty.put("totalMembers", 0);
                empty.put("totalProjects", 0);
                empty.put("totalTasks", 0);
                return Result.success(empty);
            }

            int totalTeams = userTeams.size();
            
            // 统计团队成员
            Set<Long> memberUserIds = new HashSet<>();
            for (Team team : userTeams) {
                List<TeamMember> members = teamMemberService.findByTeamId(team.getId());
                for (TeamMember m : members) {
                    if (m.getUserId() != null) {
                        memberUserIds.add(m.getUserId());
                    }
                }
            }

            int totalMembers = memberUserIds.size();
            // 暂时使用团队数量作为项目数量
            int totalProjects = totalTeams;

            // 统计任务数量（暂时使用所有任务数量，后续可以根据团队关联的项目来统计）
            List<Task> allTasks = taskService.findall();
            int totalTasks = allTasks.size();

            Map<String, Integer> overview = new HashMap<>();
            overview.put("totalTeams", totalTeams);
            overview.put("totalMembers", totalMembers);
            overview.put("totalProjects", totalProjects);
            overview.put("totalTasks", totalTasks);

            return Result.success(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取团队概览统计失败: " + e.getMessage());
        }
    }

    /**
     * 所属团队列表：只返回当前登录用户所在团队
     */
    @Operation(summary = "获取所属团队列表", description = "仅返回当前登录用户所在团队列表")
    @GetMapping("/my-teams")
    public Result getMyTeams(@RequestParam String username) {
        try {
            Optional<User> userOpt = userService.findById(username);
            if (userOpt.isEmpty()) {
                return Result.error("用户不存在");
            }

            // 与 WorkbenchController 保持一致，直接使用 username 转换为 Long 来获取团队数据
            Long userId = Long.parseLong(userOpt.get().getUsername());
            List<Team> userTeams = teamService.findTeamsByUserId(userId);

            List<Map<String, Object>> teams = new ArrayList<>();

            if (userTeams.isEmpty()) {
                return Result.success(teams);
            }

            // 预先拉取所有任务，用于统计任务数量
            List<Task> allTasks = taskService.findall();

            for (Team team : userTeams) {
                // 团队成员数
                List<TeamMember> teamMembers = teamMemberService.findByTeamId(team.getId());
                int memberCount = teamMembers.size();

                // 项目数：暂时使用1，后续可以根据团队关联的项目来统计
                int projectCount = 1;

                // 任务数：暂时使用所有任务数量，后续可以根据团队关联的项目来统计
                int taskCount = allTasks.size();

                // 构建团队成员详情列表，包含角色
                List<Map<String, Object>> memberDetails = new ArrayList<>();
                for (TeamMember tm : teamMembers) {
                    if (tm.getUserId() == null) continue;
                    String candidateUsername = tm.getUserId().toString();
                    Optional<User> memberUserOpt = userService.findById(candidateUsername);
                    if (memberUserOpt.isPresent()) {
                        Map<String, Object> memberInfo = new HashMap<>();
                        memberInfo.put("userId", tm.getUserId());
                        memberInfo.put("username", memberUserOpt.get().getUsername());
                        memberInfo.put("name", memberUserOpt.get().getName());
                        memberInfo.put("role", tm.getRoleInTeam());
                        memberDetails.add(memberInfo);
                    }
                }

                Map<String, Object> teamMap = new HashMap<>();
                teamMap.put("name", team.getName());
                teamMap.put("members", memberCount);
                teamMap.put("memberDetails", memberDetails);
                teamMap.put("projects", projectCount);
                teamMap.put("tasks", taskCount);

                teams.add(teamMap);
            }

            return Result.success(teams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取所属团队列表失败: " + e.getMessage());
        }
    }
}

