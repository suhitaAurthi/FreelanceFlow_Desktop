package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {
    
    public static int createTeam(String name, String description, String skills, int maxMembers, int adminId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return -1;
        }
        
        String sql = "INSERT INTO teams (name, description, skills, max_members, admin_id, created_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, skills);
            pstmt.setInt(4, maxMembers);
            pstmt.setInt(5, adminId);
            pstmt.setString(6, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int teamId = rs.getInt(1);
                        
                        // Add admin as first team member
                        addTeamMember(teamId, adminId, "Admin");
                        
                        return teamId;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error creating team: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public static boolean addTeamMember(int teamId, int userId, String role) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return false;
        }
        
        String sql = "INSERT INTO team_members (team_id, user_id, role, joined_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, role);
            pstmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error adding team member: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static Team getTeamById(int teamId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return null;
        }
        
        String sql = "SELECT * FROM teams WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Team(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("skills"),
                    rs.getInt("max_members"),
                    rs.getInt("admin_id"),
                    rs.getString("created_date")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting team: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<Team> getUserTeams(int userId) {
        List<Team> teams = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return teams;
        }
        
        String sql = """
            SELECT t.* FROM teams t
            INNER JOIN team_members tm ON t.id = tm.team_id
            WHERE tm.user_id = ?
            ORDER BY t.created_date DESC
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                teams.add(new Team(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("skills"),
                    rs.getInt("max_members"),
                    rs.getInt("admin_id"),
                    rs.getString("created_date")
                ));
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting user teams: " + e.getMessage());
            e.printStackTrace();
        }
        
        return teams;
    }
    
    public static String getUserTeamRole(int teamId, int userId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return null;
        }
        
        String sql = "SELECT role FROM team_members WHERE team_id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role");
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting user team role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean isUserInTeam(int teamId, int userId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM team_members WHERE team_id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking team membership: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static Team getUserFirstTeam(int userId) {
        List<Team> teams = getUserTeams(userId);
        return teams.isEmpty() ? null : teams.get(0);
    }
    
    public static List<User> getTeamMembers(int teamId) {
        List<User> members = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return members;
        }
        
        String sql = """
            SELECT u.* FROM users u
            INNER JOIN team_members tm ON u.id = tm.user_id
            WHERE tm.team_id = ?
            ORDER BY tm.joined_date ASC
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                members.add(user);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting team members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return members;
    }
    
    public static boolean removeMember(int teamId, int userId) {
        Connection conn = DatabaseManager.getConnection();
        
        if (conn == null) {
            System.out.println("Database connection is null!");
            return false;
        }
        
        String sql = "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error removing team member: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
