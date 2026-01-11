package com.example.freelanceflow_desktop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO {
    
    /**
     * Create a service request when client requests collaboration
     */
    public static int createServiceRequest(int serviceId, int clientId) {
        String sql = """
            INSERT INTO service_requests (service_id, client_id, status, request_date)
            VALUES (?, ?, 'Pending', datetime('now'))
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, clientId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating service request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Check if client has already requested this service
     */
    public static boolean hasRequested(int serviceId, int clientId) {
        String sql = "SELECT COUNT(*) FROM service_requests WHERE service_id = ? AND client_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, clientId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking service request: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get all requests for a specific service
     */
    public static List<ServiceRequest> getRequestsByServiceId(int serviceId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM service_requests WHERE service_id = ? ORDER BY request_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ServiceRequest request = new ServiceRequest(
                    rs.getInt("id"),
                    rs.getInt("service_id"),
                    rs.getInt("client_id"),
                    rs.getString("status"),
                    rs.getString("request_date"),
                    rs.getString("response_date")
                );
                requests.add(request);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching service requests: " + e.getMessage());
        }
        
        return requests;
    }
    
    /**
     * Approve service request and notify client
     */
    public static boolean approveRequest(int requestId, int serviceId, int clientId) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            
            // Update request status
            String updateSql = "UPDATE service_requests SET status = 'Approved', response_date = datetime('now') WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, requestId);
                pstmt.executeUpdate();
            }
            
            // Get service details
            ServicePost service = ServicePostDAO.getServicePostById(serviceId);
            if (service == null) {
                conn.rollback();
                return false;
            }
            
            // Notify client
            String message = "Your collaboration request for \"" + service.getTitle() + "\" has been approved! The team will contact you soon.";
            NotificationDAO.createNotification(clientId, "service_approved", message, String.valueOf(serviceId));
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error approving request: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Reject service request
     */
    public static boolean rejectRequest(int requestId, int clientId) {
        String sql = "UPDATE service_requests SET status = 'Rejected', response_date = datetime('now') WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, requestId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Notify client
                NotificationDAO.createNotification(clientId, "service_rejected", 
                    "Your collaboration request has been declined.", String.valueOf(requestId));
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error rejecting request: " + e.getMessage());
        }
        
        return false;
    }
}
