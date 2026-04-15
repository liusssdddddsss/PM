package com.example.springboot.service;

import com.example.springboot.entity.TeamAnnouncement;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

@Service
public class TeamAnnouncementService {

    @PersistenceContext
    private EntityManager entityManager;

    // 获取所有公告
    public List<TeamAnnouncement> findAll() {
        Query query = entityManager.createQuery("SELECT a FROM TeamAnnouncement a ORDER BY a.createdAt DESC");
        return query.getResultList();
    }

    // 根据团队ID获取公告
    public List<TeamAnnouncement> findByTeamId(Integer teamId) {
        Query query = entityManager.createQuery("SELECT a FROM TeamAnnouncement a WHERE a.teamId = :teamId ORDER BY a.createdAt DESC");
        query.setParameter("teamId", teamId);
        return query.getResultList();
    }

    // 保存公告
    public TeamAnnouncement save(TeamAnnouncement announcement) {
        if (announcement.getId() == null) {
            entityManager.persist(announcement);
        } else {
            announcement = entityManager.merge(announcement);
        }
        return announcement;
    }

    // 删除公告
    public void deleteById(Integer id) {
        TeamAnnouncement announcement = entityManager.find(TeamAnnouncement.class, id);
        if (announcement != null) {
            entityManager.remove(announcement);
        }
    }

    // 根据ID获取公告
    public TeamAnnouncement findById(Integer id) {
        return entityManager.find(TeamAnnouncement.class, id);
    }
}