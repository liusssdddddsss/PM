package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.Task;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Project;
import com.example.springboot.service.TeamService;
import com.example.springboot.service.TeamMemberService;
import com.example.springboot.service.TaskService;
import com.example.springboot.service.UserService;
import com.example.springboot.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Resource
    private ProjectService projectService;

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
            
            // 统计项目数量：获取所有项目，然后统计数量
            Iterable<Project> projects = projectService.findAll();
            int totalProjects = 0;
            for (Project project : projects) {
                totalProjects++;
            }

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

                // 项目数：获取所有项目，然后统计数量
                Iterable<Project> projects = projectService.findAll();
                int projectCount = 0;
                for (Project project : projects) {
                    projectCount++;
                }

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

    /**
     * 创建团队
     */
    @Operation(summary = "创建团队", description = "创建新团队")
    @PostMapping("/create")
    public Result createTeam(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            String username = (String) request.get("username");

            if (name == null || name.isEmpty()) {
                return Result.error("团队名称不能为空");
            }

            Optional<User> userOpt = userService.findById(username);
            if (userOpt.isEmpty()) {
                return Result.error("用户不存在");
            }

            // 创建团队
            Team team = new Team();
            team.setName(name);
            team.setDescription(description);
            team.setCreatorId(Long.parseLong(userOpt.get().getUsername()));
            team.setCreatedAt(new Date());
            team = teamService.save(team);

            // 将创建者添加为团队成员
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(team.getId());
            teamMember.setUserId(Long.parseLong(userOpt.get().getUsername()));
            teamMember.setRoleInTeam("项目经理");
            teamMember.setJoinedAt(new Date());
            teamMemberService.save(teamMember);

            return Result.success(team);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建团队失败");
        }
    }

    /**
     * 邀请成员
     */
    @Operation(summary = "邀请成员", description = "邀请成员加入团队")
    @PostMapping("/invite")
    public Result inviteMember(@RequestBody Map<String, Object> request) {
        try {
            String teamName = (String) request.get("teamId");
            String username = (String) request.get("username");
            String role = (String) request.get("role");

            if (teamName == null || teamName.isEmpty()) {
                return Result.error("团队名称不能为空");
            }

            if (username == null || username.isEmpty()) {
                return Result.error("用户名不能为空");
            }

            if (role == null || role.isEmpty()) {
                return Result.error("角色不能为空");
            }

            // 查找团队
            List<Team> teams = teamService.findAll();
            Team targetTeam = null;
            for (Team team : teams) {
                if (team.getName().equals(teamName)) {
                    targetTeam = team;
                    break;
                }
            }

            if (targetTeam == null) {
                return Result.error("团队不存在");
            }

            // 查找用户
            Optional<User> userOpt = userService.findById(username);
            if (userOpt.isEmpty()) {
                return Result.error("用户不存在");
            }

            // 检查用户是否已经是团队成员
            List<TeamMember> existingMembers = teamMemberService.findByTeamId(targetTeam.getId());
            for (TeamMember member : existingMembers) {
                if (member.getUserId().equals(Long.parseLong(username))) {
                    return Result.error("用户已经是团队成员");
                }
            }

            // 添加团队成员
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(targetTeam.getId());
            teamMember.setUserId(Long.parseLong(username));
            teamMember.setRoleInTeam(role);
            teamMember.setJoinedAt(new Date());
            teamMemberService.save(teamMember);

            return Result.success(teamMember);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("邀请成员失败: " + e.getMessage());
        }
    }
}

