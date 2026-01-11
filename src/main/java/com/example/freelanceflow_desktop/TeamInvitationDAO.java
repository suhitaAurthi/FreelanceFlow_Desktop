package com.example.freelanceflow_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TeamInvitationDAO {
    
    public static int createInvitation(int teamId, int userId, int invitedBy) {
        String sql = "INSERT INTO team_invitations (team_id, user_id, invited_by, status, invited_date) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return -1;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, invitedBy);
            pstmt.setString(4, "Pending");
            pstmt.setString(5, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int invitationId = generatedKeys.getInt(1);
                        System.out.println("Team invitation created successfully with ID: " + invitationId);
                        return invitationId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating team invitation: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    public static List<TeamInvitation> getUserPendingInvitations(int userId) {
        String sql = "SELECT * FROM team_invitations WHERE user_id = ? AND status = 'Pending' ORDER BY invited_date DESC";
        List<TeamInvitation> invitations = new ArrayList<>();
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null!");
            return invitations;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TeamInvitation invitation = new TeamInvitation();
                invitation.setId(rs.getInt("id"));
                invitation.setTeamId(rs.getInt("team_id"));
                invitation.setUserId(rs.getInt("user_id"));
                invitation.setInvitedBy(rs.getInt("invited_by"));
                invitation.setStatus(rs.getString("status"));
                invitation.setInvitedDate(rs.getString("invited_date"));
                invitation.setResponseDate(rs.getString("response_date"));
                invitations.add(invitation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user invitations: " + e.getMessage());
            e.printStackTrace();
        }
        return invitations;
    }
    
    public static boolean acceptInvitation(int invitationId) {
        String sql = "UPDATE team_invitations SET status = 'Accepted', response_date = ? WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setInt(2, invitationId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error accepting invitation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean rejectInvitation(int invitationId) {
        String sql = "UPDATE team_invitations SET status = 'Rejected', response_date = ? WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setInt(2, invitationId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting invitation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean hasPendingInvitation(int teamId, int userId) {
        String sql = "SELECT COUNT(*) FROM team_invitations WHERE team_id = ? AND user_id = ? AND status = 'Pending'";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking pending invitation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public static TeamInvitation getInvitationById(int invitationId) {
        String sql = "SELECT * FROM team_invitations WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            return null;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invitationId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                TeamInvitation invitation = new TeamInvitation();
                invitation.setId(rs.getInt("id"));
                invitation.setTeamId(rs.getInt("team_id"));
                invitation.setUserId(rs.getInt("user_id"));
                invitation.setInvitedBy(rs.getInt("invited_by"));
                invitation.setStatus(rs.getString("status"));
                invitation.setInvitedDate(rs.getString("invited_date"));
                invitation.setResponseDate(rs.getString("response_date"));
                return invitation;
            }
        } catch (SQLException e) {
            System.err.println("Error getting invitation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
