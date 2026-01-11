package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecruitmentPostDAO {
    
    public static int createRecruitmentPost(int teamId, String title, String description, String requiredSkills, int positions, int createdBy) {
        String sql = "INSERT INTO recruitment_posts (team_id, title, description, required_skills, positions, status, created_by, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return -1;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, teamId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, requiredSkills);
            pstmt.setInt(5, positions);
            pstmt.setString(6, "Open");
            pstmt.setInt(7, createdBy);
            pstmt.setString(8, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int postId = generatedKeys.getInt(1);
                        System.out.println("Recruitment post created successfully with ID: " + postId);
                        return postId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating recruitment post: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    public static List<RecruitmentPost> getTeamRecruitmentPosts(int teamId) {
        String sql = "SELECT * FROM recruitment_posts WHERE team_id = ? ORDER BY created_date DESC";
        List<RecruitmentPost> posts = new ArrayList<>();
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return posts;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RecruitmentPost post = new RecruitmentPost();
                post.setId(rs.getInt("id"));
                post.setTeamId(rs.getInt("team_id"));
                post.setTitle(rs.getString("title"));
                post.setDescription(rs.getString("description"));
                post.setRequiredSkills(rs.getString("required_skills"));
                post.setPositions(rs.getInt("positions"));
                post.setStatus(rs.getString("status"));
                post.setCreatedBy(rs.getInt("created_by"));
                post.setCreatedDate(rs.getString("created_date"));
                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println("Error getting recruitment posts: " + e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }
    
    public static List<RecruitmentPost> getAllOpenRecruitmentPosts() {
        String sql = "SELECT * FROM recruitment_posts WHERE status = 'Open' ORDER BY created_date DESC";
        List<RecruitmentPost> posts = new ArrayList<>();
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return posts;
        }
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RecruitmentPost post = new RecruitmentPost();
                post.setId(rs.getInt("id"));
                post.setTeamId(rs.getInt("team_id"));
                post.setTitle(rs.getString("title"));
                post.setDescription(rs.getString("description"));
                post.setRequiredSkills(rs.getString("required_skills"));
                post.setPositions(rs.getInt("positions"));
                post.setStatus(rs.getString("status"));
                post.setCreatedBy(rs.getInt("created_by"));
                post.setCreatedDate(rs.getString("created_date"));
                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println("Error getting open recruitment posts: " + e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }
    
    public static boolean updateRecruitmentStatus(int postId, String newStatus) {
        String sql = "UPDATE recruitment_posts SET status = ? WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, postId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating recruitment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean deleteRecruitmentPost(int postId) {
        String sql = "DELETE FROM recruitment_posts WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting recruitment post: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
