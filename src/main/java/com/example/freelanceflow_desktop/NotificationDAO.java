package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    public static boolean createNotification(int userId, String type, String message, String relatedId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return false;
        }
        
        String sql = "INSERT INTO notifications (user_id, type, message, related_id, is_read, created_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setString(3, message);
            pstmt.setString(4, relatedId);
            pstmt.setBoolean(5, false);
            pstmt.setString(6, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static List<Notification> getUserNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return notifications;
        }
        
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_date DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(new Notification(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("type"),
                    rs.getString("message"),
                    rs.getString("related_id"),
                    rs.getBoolean("is_read"),
                    rs.getString("created_date")
                ));
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting notifications: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    public static int getUnreadCount(int userId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting unread count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static boolean markAsRead(int notificationId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return false;
        }
        
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
