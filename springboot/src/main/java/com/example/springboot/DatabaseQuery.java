package com.example.springboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseQuery {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pm?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "emo1231";
        
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 连接数据库
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            
            // 查询202201用户
            System.out.println("查询用户202201...");
            ResultSet rs = stmt.executeQuery("SELECT id, username, name FROM user WHERE username = '202201'");
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                String userUsername = rs.getString("username");
                String userName = rs.getString("name");
                System.out.println("找到用户: " + userUsername + " (ID: " + userId + ", 姓名: " + userName + ")");
                
                // 查询用户的任务
                System.out.println("\n查询用户的任务...");
                ResultSet taskRs = stmt.executeQuery("SELECT id, title FROM tasks WHERE assigneeId = " + userId);
                int taskCount = 0;
                while (taskRs.next()) {
                    int taskId = taskRs.getInt("id");
                    String taskTitle = taskRs.getString("title");
                    System.out.println("任务ID: " + taskId + ", 标题: " + taskTitle);
                    taskCount++;
                }
                System.out.println("用户202201的任务数量: " + taskCount);
                taskRs.close();
                
                // 查询用户的Bug
                System.out.println("\n查询用户的Bug...");
                ResultSet bugRs = stmt.executeQuery("SELECT id, title FROM bugs WHERE assigneeId = " + userId);
                int bugCount = 0;
                while (bugRs.next()) {
                    int bugId = bugRs.getInt("id");
                    String bugTitle = bugRs.getString("title");
                    System.out.println("Bug ID: " + bugId + ", 标题: " + bugTitle);
                    bugCount++;
                }
                System.out.println("用户202201的Bug数量: " + bugCount);
                bugRs.close();
            } else {
                System.out.println("未找到用户202201");
            }
            
            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}