package com.example.freelanceflow_desktop;

import java.sql.*;

public class FreelancerDetailsDAO {
    
    public static boolean saveFreelancerDetails(int userId, String displayName, String phone, 
                                                String location, String ratePerHour, String profilePhotoPath) {
        String sql = "INSERT INTO freelancer_details (user_id, display_name, phone, location, rate_per_hour, profile_photo_path) " +
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
            pstmt.setString(5, ratePerHour);
            pstmt.setString(6, profilePhotoPath);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Freelancer details saved successfully!");
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving freelancer details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
