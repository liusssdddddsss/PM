package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.User;
import com.example.springboot.repository.ProjectRepository;
import com.example.springboot.repository.TeamMemberRepository;
import com.example.springboot.repository.TeamRepository;
import com.example.springboot.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/teams")
public class AdminTeamController {

    @Resource
    private TeamRepository teamRepository;
    @Resource
    private TeamMemberRepository teamMemberRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private ProjectRepository projectRepository;

    @GetMapping
    public Result listTeams() {
        try {
            List<Team> teams = teamRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Team t : teams) {
                result.add(toTeamListRow(t));
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询团队失败: " + e.getMessage());
        }
    }

    @GetMapping("/{teamId}")
    public Result teamDetail(@PathVariable Integer teamId) {
        try {
            Optional<Team> opt = teamRepository.findById(teamId);
            if (opt.isEmpty()) return Result.error("团队不存在");
            Team t = opt.get();

            Map<String, Object> row = toTeamListRow(t);
            row.put("members", toTeamMembers(t.getId()));
            return Result.success(row);
        } catch (Exception e) {
            return Result.error("查询团队详情失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result createTeam(@RequestBody AdminTeamCreateRequest request) {
        try {
            if (request == null || request.teamName == null) {
                return Result.error("参数不完整");
            }

            Team t = new Team();
            t.setName(request.teamName);
            t.setDescription(request.projectName);
            t.setCreatorId(request.creatorId);
            t.setCreatedAt(request.createTime != null ? request.createTime : new Date());
            t.setUpdatedAt(request.deadline != null ? request.deadline : new Date());

            Team saved = teamRepository.save(t);

            // 可选：根据 leaderName 自动创建队长成员
            if (request.leaderName != null && !request.leaderName.isEmpty()) {
                User leader = findUserByName(request.leaderName);
                if (leader != null && leader.getId() != null) {
                    TeamMember tm = new TeamMember();
                    tm.setTeamId(saved.getId());
                    tm.setUserId(leader.getId().longValue());
                    tm.setRoleInTeam("项目经理");
                    tm.setJoinedAt(new Date());
                    tm.setJoinedBy(request.creatorId);
                    teamMemberRepository.save(tm);
                }
            }

            return Result.success(saved);
        } catch (Exception e) {
            return Result.error("创建团队失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}")
    public Result disbandTeam(@PathVariable Integer teamId) {
        try {
            // 先删成员，避免外键约束问题
            teamMemberRepository.deleteByTeamId(teamId);
            teamRepository.deleteById(teamId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("解散团队失败: " + e.getMessage());
        }
    }

    private Map<String, Object> toTeamListRow(Team t) {
        System.out.println("========== 处理团队开始 ==========");
        System.out.println("团队ID: " + t.getId());
        System.out.println("团队名称: " + t.getName());
        System.out.println("团队描述: " + t.getDescription());
        
        Map<String, Object> row = new HashMap<>();
        row.put("teamId", t.getId() == null ? "" : String.valueOf(t.getId()));
        row.put("teamName", t.getName());
        
        // 从项目表中获取关联的项目名称
        Long teamIdLong = t.getId() != null ? t.getId().longValue() : null;
        System.out.println("查询团队ID为 " + teamIdLong + " 的项目...");
        List<Project> projects = projectRepository.findByTeamId(teamIdLong);
        System.out.println("找到的项目数量: " + projects.size());
        for (Project p : projects) {
            System.out.println("  - 项目ID: " + p.getId() + ", 项目名称: " + p.getName() + ", 团队ID: " + p.getTeam_id());
        }
        
        String projectNames = projects.stream()
                .map(Project::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("、"));
        System.out.println("合并后的项目名称: " + projectNames);
        row.put("projectName", projectNames.isEmpty() ? "" : projectNames);

        // 为每个团队添加真实的进度信息
        Integer progress = 0;
        String progressNode = "";
        if (!projects.isEmpty()) {
            // 计算平均进度
            int totalProgress = projects.stream().mapToInt(p -> p.getProgress() != null ? p.getProgress() : 0).sum();
            progress = totalProgress / projects.size();
            progressNode = "进行中";
        } else {
            progress = 0;
            progressNode = "初始化";
        }
        System.out.println("进度: " + progress + ", 阶段: " + progressNode);
        row.put("progress", progress);
        row.put("progressNode", progressNode);

        // 添加团队成员信息
        System.out.println("查询团队成员...");
        List<Map<String, Object>> members = toTeamMembers(t.getId());
        System.out.println("团队成员数量: " + members.size());
        for (Map<String, Object> m : members) {
            System.out.println("  - 成员: " + m.get("name") + ", 角色: " + m.get("role") + ", 职位: " + m.get("position"));
        }
        row.put("members", members);

        row.put("createTime", formatDate(t.getCreatedAt()));
        row.put("deadline", formatDate(t.getUpdatedAt()));
        String leader = resolveLeaderName(t.getId());
        System.out.println("团队领导: " + leader);
        row.put("leader", leader);
        
        System.out.println("========== 处理团队结束 ==========");
        return row;
    }

    private List<Map<String, Object>> toTeamMembers(Integer teamId) {
        System.out.println("  ---------- toTeamMembers 开始, teamId: " + teamId + " ----------");
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        System.out.println("  找到的 TeamMember 数量: " + members.size());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeamMember tm : members) {
            System.out.println("  处理 TeamMember: id=" + tm.getId() + ", userId=" + tm.getUserId() + ", roleInTeam=" + tm.getRoleInTeam());
            
            if (tm.getUserId() == null) {
                System.out.println("  跳过: userId 为空");
                continue;
            }
            
            // 转换 userId 为字符串，因为 User 实体的主键是 username（String 类型）
            String userIdStr = String.valueOf(tm.getUserId());
            System.out.println("  用 userIdStr: " + userIdStr + " 查询 User...");
            User u = userRepository.findById(userIdStr).orElse(null);
            System.out.println("  查询到的 User: " + (u != null ? "id=" + u.getId() + ", name=" + u.getName() + ", is_admin=" + u.getIs_admin() + ", role_id=" + u.getRole_id() : "null"));
            
            Map<String, Object> m = new HashMap<>();
            m.put("name", u != null ? u.getName() : "");
            m.put("role", tm.getRoleInTeam());
            m.put("position", mapPosition(u != null ? u.getIs_admin() : null, u != null ? u.getRole_id() : null));
            System.out.println("  添加到结果: name=" + m.get("name") + ", role=" + m.get("role") + ", position=" + m.get("position"));
            result.add(m);
        }
        
        System.out.println("  ---------- toTeamMembers 结束, 返回 " + result.size() + " 个成员 ----------");
        return result;
    }

    private String resolveLeaderName(Integer teamId) {
        System.out.println("  ---------- resolveLeaderName 开始, teamId: " + teamId + " ----------");
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        System.out.println("  团队成员数量: " + (members != null ? members.size() : 0));
        
        if (members == null || members.isEmpty()) {
            System.out.println("  没有团队成员，返回空字符串");
            return "";
        }
        
        // 优先：项目经理/leader
        TeamMember leader = members.stream()
                .filter(m -> m.getRoleInTeam() != null && (m.getRoleInTeam().contains("leader") || m.getRoleInTeam().contains("项目经理")))
                .findFirst()
                .orElse(members.get(0));
        System.out.println("  选中的 leader: userId=" + leader.getUserId() + ", roleInTeam=" + leader.getRoleInTeam());

        if (leader.getUserId() == null) {
            System.out.println("  leader 的 userId 为空，返回空字符串");
            return "";
        }
        
        // 转换 userId 为字符串，因为 User 实体的主键是 username（String 类型）
        String userIdStr = String.valueOf(leader.getUserId());
        System.out.println("  查询 User, userIdStr: " + userIdStr);
        User u = userRepository.findById(userIdStr).orElse(null);
        String result = u != null ? u.getName() : "";
        System.out.println("  resolveLeaderName 结果: " + result);
        System.out.println("  ---------- resolveLeaderName 结束 ----------");
        return result;
    }

    private static String formatDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    private User findUserByName(String name) {
        // 简化：直接遍历全量用户（项目规模不大时可用）
        for (User u : userRepository.findAll()) {
            if (u.getName() != null && u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    private static String mapPosition(Integer isAdmin, Long roleId) {
        if (isAdmin != null && isAdmin == 1) return "管理员";
        if (roleId == null) return "用户";
        return switch (roleId.intValue()) {
            case 1 -> "产品经理";
            case 2 -> "开发者";
            case 3 -> "测试者";
            default -> "用户";
        };
    }

    public static class AdminTeamCreateRequest {
        public String teamName;
        public String leaderName;
        public String projectName;
        public Date createTime;
        public Date deadline;
        public Long creatorId;
    }
}

