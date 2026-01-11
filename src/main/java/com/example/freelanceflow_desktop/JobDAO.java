package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Create a new job posting
     */
    public static boolean createJob(Job job) {
        String sql = """
            INSERT INTO jobs (client_id, title, description, budget, duration, skills, status, posted_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, job.getClientId());
            pstmt.setString(2, job.getTitle());
            pstmt.setString(3, job.getDescription());
            pstmt.setString(4, job.getBudget());
            pstmt.setString(5, job.getDuration());
            pstmt.setString(6, job.getSkills());
            pstmt.setString(7, job.getStatus());
            pstmt.setString(8, LocalDateTime.now().format(formatter));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating job: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all jobs posted by a specific client
     */
    public static List<Job> getJobsByClientId(int clientId) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE client_id = ? ORDER BY posted_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setClientId(rs.getInt("client_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setBudget(rs.getString("budget"));
                job.setDuration(rs.getString("duration"));
                job.setSkills(rs.getString("skills"));
                job.setStatus(rs.getString("status"));
                job.setPostedDate(rs.getString("posted_date"));
                jobs.add(job);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching client jobs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return jobs;
    }
    
    /**
     * Get all open jobs (available for teams to see)
     */
    public static List<Job> getAllOpenJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE status = 'Open' ORDER BY posted_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setClientId(rs.getInt("client_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setBudget(rs.getString("budget"));
                job.setDuration(rs.getString("duration"));
                job.setSkills(rs.getString("skills"));
                job.setStatus(rs.getString("status"));
                job.setPostedDate(rs.getString("posted_date"));
                jobs.add(job);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching open jobs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return jobs;
    }
    
    /**
     * Get a job by ID
     */
    public static Job getJobById(int jobId) {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setClientId(rs.getInt("client_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setBudget(rs.getString("budget"));
                job.setDuration(rs.getString("duration"));
                job.setSkills(rs.getString("skills"));
                job.setStatus(rs.getString("status"));
                job.setPostedDate(rs.getString("posted_date"));
                return job;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching job: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update job status
     */
    public static boolean updateJobStatus(int jobId, String newStatus) {
        String sql = "UPDATE jobs SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, jobId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating job status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a job
     */
    public static boolean deleteJob(int jobId) {
        String sql = "DELETE FROM jobs WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting job: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get client name by user ID
     */
    public static String getClientName(int userId) {
        String sql = """
            SELECT u.first_name, u.last_name, cd.display_name 
            FROM users u 
            LEFT JOIN client_details cd ON u.id = cd.user_id 
            WHERE u.id = ?
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String displayName = rs.getString("display_name");
                if (displayName != null && !displayName.isEmpty()) {
                    return displayName;
                }
                return rs.getString("first_name") + " " + rs.getString("last_name");
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching client name: " + e.getMessage());
        }
        
        return "Unknown Client";
    }
}
