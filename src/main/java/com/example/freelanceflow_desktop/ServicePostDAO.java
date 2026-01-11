package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServicePostDAO {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Create a new service post
     */
    public static boolean createServicePost(ServicePost service) {
        String sql = """
            INSERT INTO service_posts (team_id, title, description, category, pricing, 
                                      delivery_time, status, created_by, created_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, service.getTeamId());
            pstmt.setString(2, service.getTitle());
            pstmt.setString(3, service.getDescription());
            pstmt.setString(4, service.getCategory());
            pstmt.setString(5, service.getPricing());
            pstmt.setString(6, service.getDeliveryTime());
            pstmt.setString(7, service.getStatus());
            pstmt.setInt(8, service.getCreatedBy());
            pstmt.setString(9, LocalDateTime.now().format(formatter));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating service post: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all service posts for a specific team
     */
    public static List<ServicePost> getTeamServicePosts(int teamId) {
        List<ServicePost> services = new ArrayList<>();
        String sql = "SELECT * FROM service_posts WHERE team_id = ? ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ServicePost service = extractServiceFromResultSet(rs);
                services.add(service);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching team service posts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    /**
     * Get all active service posts (for clients to browse)
     */
    public static List<ServicePost> getAllActiveServicePosts() {
        List<ServicePost> services = new ArrayList<>();
        String sql = "SELECT * FROM service_posts WHERE status = 'Active' ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ServicePost service = extractServiceFromResultSet(rs);
                services.add(service);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching active service posts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return services;
    }
    
    /**
     * Update service post status
     */
    public static boolean updateServiceStatus(int serviceId, String newStatus) {
        String sql = "UPDATE service_posts SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, serviceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating service status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a service post
     */
    public static boolean deleteServicePost(int serviceId) {
        String sql = "DELETE FROM service_posts WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting service post: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get service post by ID
     */
    public static ServicePost getServicePostById(int serviceId) {
        String sql = "SELECT * FROM service_posts WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractServiceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching service post: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get team name by team ID
     */
    public static String getTeamName(int teamId) {
        String sql = "SELECT name FROM teams WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching team name: " + e.getMessage());
        }
        
        return "Unknown Team";
    }
    
    private static ServicePost extractServiceFromResultSet(ResultSet rs) throws SQLException {
        ServicePost service = new ServicePost();
        service.setId(rs.getInt("id"));
        service.setTeamId(rs.getInt("team_id"));
        service.setTitle(rs.getString("title"));
        service.setDescription(rs.getString("description"));
        service.setCategory(rs.getString("category"));
        service.setPricing(rs.getString("pricing"));
        service.setDeliveryTime(rs.getString("delivery_time"));
        service.setStatus(rs.getString("status"));
        service.setCreatedBy(rs.getInt("created_by"));
        service.setCreatedDate(rs.getString("created_date"));
        return service;
    }
}
