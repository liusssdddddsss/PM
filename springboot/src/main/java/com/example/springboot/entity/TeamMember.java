package com.example.springboot.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "team_members")
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "team_id")
    private Integer teamId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "role_in_team", columnDefinition = "VARCHAR(50) NOT NULL")
    private String roleInTeam;
    @Column(name = "joined_at")
    private Date joinedAt;
    @Column(name = "joined_by")
    private Long joinedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleInTeam() {
        return roleInTeam;
    }

    public void setRoleInTeam(String roleInTeam) {
        this.roleInTeam = roleInTeam;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Long getJoinedBy() {
        return joinedBy;
    }

    public void setJoinedBy(Long joinedBy) {
        this.joinedBy = joinedBy;
    }
}
