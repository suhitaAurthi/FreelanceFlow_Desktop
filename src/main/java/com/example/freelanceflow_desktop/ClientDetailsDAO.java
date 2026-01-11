package com.example.freelanceflow_desktop;

import java.sql.*;

public class ClientDetailsDAO {
    
    public static boolean saveClientDetails(int userId, String displayName, String phone, 
                                            String location, String projectBudget, String companyLogoPath) {
        String sql = "INSERT INTO client_details (user_id, display_name, phone, location, project_budget, company_logo_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, displayName);
            pstmt.setString(3, phone);
            pstmt.setString(4, location);
            pstmt.setString(5, projectBudget);
            pstmt.setString(6, companyLogoPath);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Client details saved successfully!");
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving client details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get profile photo path for a client user
     */
    public static String getProfilePhotoPath(int userId) {
        String sql = "SELECT company_logo_path FROM client_details WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("company_logo_path");
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching client photo: " + e.getMessage());
        }
        
        return null;
    }}