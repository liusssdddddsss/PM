package com.example.springboot.service;

import com.example.springboot.entity.Message;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

@Service
public class MessageService {

    @PersistenceContext
    private EntityManager entityManager;

    // 获取所有消息
    public List<Message> findAll() {
        Query query = entityManager.createQuery("SELECT m FROM Message m ORDER BY m.createdAt DESC");
        return query.getResultList();
    }

    // 根据接收者获取消息
    public List<Message> findByReceiver(String receiver) {
        Query query = entityManager.createQuery("SELECT m FROM Message m WHERE m.receiver = :receiver ORDER BY m.createdAt DESC");
        query.setParameter("receiver", receiver);
        return query.getResultList();
    }

    // 根据团队ID获取消息
    public List<Message> findByTeamId(Integer teamId) {
        Query query = entityManager.createQuery("SELECT m FROM Message m WHERE m.teamId = :teamId ORDER BY m.createdAt DESC");
        query.setParameter("teamId", teamId);
        return query.getResultList();
    }

    // 获取未读消息
    public List<Message> findUnreadByReceiver(String receiver) {
        Query query = entityManager.createQuery("SELECT m FROM Message m WHERE m.receiver = :receiver AND m.isRead = 0 ORDER BY m.createdAt DESC");
        query.setParameter("receiver", receiver);
        return query.getResultList();
    }

    // 保存消息
    public Message save(Message message) {
        if (message.getId() == null) {
            entityManager.persist(message);
        } else {
            message = entityManager.merge(message);
        }
        return message;
    }

    // 删除消息
    public void deleteById(Integer id) {
        Message message = entityManager.find(Message.class, id);
        if (message != null) {
            entityManager.remove(message);
        }
    }

    // 根据ID获取消息
    public Message findById(Integer id) {
        return entityManager.find(Message.class, id);
    }

    // 标记消息为已读
    public void markAsRead(Integer id) {
        Message message = entityManager.find(Message.class, id);
        if (message != null) {
            message.setIsRead(1);
            entityManager.merge(message);
        }
    }
}