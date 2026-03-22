package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Team;
import com.example.springboot.entity.TeamMember;
import com.example.springboot.entity.User;
import com.example.springboot.repository.TeamMemberRepository;
import com.example.springboot.repository.TeamRepository;
import com.example.springboot.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/admin/teams")
public class AdminTeamController {

    @Resource
    private TeamRepository teamRepository;
    @Resource
    private TeamMemberRepository teamMemberRepository;
    @Resource
    private UserRepository userRepository;

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
        Map<String, Object> row = new HashMap<>();
        row.put("teamId", t.getId() == null ? "" : String.valueOf(t.getId()));
        row.put("teamName", t.getName());
        row.put("projectName", t.getDescription());

        Integer progress = 0;
        row.put("progress", progress);
        row.put("progressNode", "");

        row.put("createTime", formatDate(t.getCreatedAt()));
        row.put("deadline", formatDate(t.getUpdatedAt()));
        row.put("leader", resolveLeaderName(t.getId()));
        return row;
    }

    private List<Map<String, Object>> toTeamMembers(Integer teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeamMember tm : members) {
            if (tm.getUserId() == null) continue;
            Integer userPk = tm.getUserId().intValue();
            User u = userRepository.findById(userPk);
            Map<String, Object> m = new HashMap<>();
            m.put("name", u != null ? u.getName() : "");
            m.put("role", tm.getRoleInTeam());
            m.put("position", mapPosition(u != null ? u.getIs_admin() : null, u != null ? u.getRole_id() : null));
            result.add(m);
        }
        return result;
    }

    private String resolveLeaderName(Integer teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        if (members == null || members.isEmpty()) {
            return "";
        }
        // 优先：项目经理/leader
        TeamMember leader = members.stream()
                .filter(m -> m.getRoleInTeam() != null && (m.getRoleInTeam().contains("leader") || m.getRoleInTeam().contains("项目经理")))
                .findFirst()
                .orElse(members.get(0));

        if (leader.getUserId() == null) return "";
        User u = userRepository.findById(leader.getUserId().intValue());
        return u != null ? u.getName() : "";
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

