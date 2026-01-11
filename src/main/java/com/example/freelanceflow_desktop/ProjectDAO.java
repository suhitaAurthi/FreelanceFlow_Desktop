package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    
    public static int createProject(int teamId, String name, String description, String startDate, String endDate, int createdBy) {
        String sql = "INSERT INTO projects (team_id, name, description, status, start_date, end_date, created_by, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return -1;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, teamId);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setString(4, "Active");
            pstmt.setString(5, startDate);
            pstmt.setString(6, endDate);
            pstmt.setInt(7, createdBy);
            pstmt.setString(8, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int projectId = generatedKeys.getInt(1);
                        System.out.println("Project created successfully with ID: " + projectId);
                        return projectId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating project: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    public static List<Project> getTeamProjects(int teamId) {
        String sql = "SELECT * FROM projects WHERE team_id = ? ORDER BY created_date DESC";
        List<Project> projects = new ArrayList<>();
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return projects;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("id"));
                project.setTeamId(rs.getInt("team_id"));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                project.setStatus(rs.getString("status"));
                project.setStartDate(rs.getString("start_date"));
                project.setEndDate(rs.getString("end_date"));
                project.setCreatedBy(rs.getInt("created_by"));
                project.setCreatedDate(rs.getString("created_date"));
                projects.add(project);
            }
        } catch (SQLException e) {
            System.err.println("Error getting team projects: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    public static int getTeamProjectCount(int teamId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE team_id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return 0;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting projects: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getActiveProjectCount(int teamId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE team_id = ? AND status = 'Active'";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return 0;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active projects: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getCompletedProjectCount(int teamId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE team_id = ? AND status = 'Completed'";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return 0;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting completed projects: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    public static boolean updateProjectStatus(int projectId, String newStatus) {
        String sql = "UPDATE projects SET status = ? WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, projectId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating project status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean deleteProject(int projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting project: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all projects for teams that a freelancer is a member of
     */
    public static List<Project> getFreelancerProjects(int freelancerId) {
        String sql = """  
            SELECT DISTINCT p.* FROM projects p
            INNER JOIN team_members tm ON p.team_id = tm.team_id
            WHERE tm.user_id = ?
            ORDER BY p.created_date DESC
        """;
        List<Project> projects = new ArrayList<>();
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return projects;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, freelancerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("id"));
                project.setTeamId(rs.getInt("team_id"));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                project.setStatus(rs.getString("status"));
                project.setStartDate(rs.getString("start_date"));
                project.setEndDate(rs.getString("end_date"));
                project.setCreatedBy(rs.getInt("created_by"));
                project.setCreatedDate(rs.getString("created_date"));
                projects.add(project);
            }
        } catch (SQLException e) {
            System.err.println("Error getting freelancer projects: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
}
