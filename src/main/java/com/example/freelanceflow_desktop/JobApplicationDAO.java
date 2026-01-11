package com.example.freelanceflow_desktop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobApplicationDAO {
    
    /**
     * Create a job application when team applies for a job
     */
    public static int createJobApplication(int jobId, int teamId) {
        String sql = """
            INSERT INTO job_applications (job_id, team_id, status, applied_date)
            VALUES (?, ?, 'Pending', datetime('now'))
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, teamId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating job application: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get all applications for a specific job
     */
    public static List<JobApplication> getApplicationsByJobId(int jobId) {
        List<JobApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM job_applications WHERE job_id = ? ORDER BY applied_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                JobApplication app = new JobApplication(
                    rs.getInt("id"),
                    rs.getInt("job_id"),
                    rs.getInt("team_id"),
                    rs.getString("status"),
                    rs.getString("applied_date"),
                    rs.getString("response_date")
                );
                applications.add(app);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching job applications: " + e.getMessage());
        }
        
        return applications;
    }
    
    /**
     * Check if team has already applied for a job
     */
    public static boolean hasApplied(int jobId, int teamId) {
        String sql = "SELECT COUNT(*) FROM job_applications WHERE job_id = ? AND team_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, teamId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking job application: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Approve job application and create project for team
     */
    public static boolean approveApplication(int applicationId, int jobId, int teamId) {
        Connection conn = null;
        try {
            System.out.println("Starting approval process for application: " + applicationId);
            
            conn = DatabaseManager.getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null");
                return false;
            }
            
            conn.setAutoCommit(false);
            System.out.println("Transaction started");
            
            // Update application status
            String updateSql = "UPDATE job_applications SET status = 'Approved', response_date = datetime('now') WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, applicationId);
                int updated = pstmt.executeUpdate();
                System.out.println("Application status updated: " + updated + " rows");
            }
            
            // Get job details using the same connection
            System.out.println("Fetching job details for jobId: " + jobId);
            Job job = null;
            String jobSql = "SELECT * FROM jobs WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(jobSql)) {
                pstmt.setInt(1, jobId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    job = new Job();
                    job.setId(rs.getInt("id"));
                    job.setClientId(rs.getInt("client_id"));
                    job.setTitle(rs.getString("title"));
                    job.setDescription(rs.getString("description"));
                    job.setBudget(rs.getString("budget"));
                    job.setDuration(rs.getString("duration"));
                    job.setSkills(rs.getString("skills"));
                    job.setStatus(rs.getString("status"));
                    job.setPostedDate(rs.getString("posted_date"));
                }
            }
            
            if (job == null) {
                System.err.println("ERROR: Job not found with id: " + jobId);
                conn.rollback();
                return false;
            }
            System.out.println("Job found: " + job.getTitle());
            
            // Get client name using the same connection
            String clientName = "Unknown Client";
            String clientSql = """
                SELECT u.first_name, u.last_name, cd.display_name 
                FROM users u 
                LEFT JOIN client_details cd ON u.id = cd.user_id 
                WHERE u.id = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(clientSql)) {
                pstmt.setInt(1, job.getClientId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String displayName = rs.getString("display_name");
                    if (displayName != null && !displayName.isEmpty()) {
                        clientName = displayName;
                    } else {
                        clientName = rs.getString("first_name") + " " + rs.getString("last_name");
                    }
                }
            }
            System.out.println("Client name: " + clientName);
            
            // Create project for the team
            String projectSql = """
                INSERT INTO projects (team_id, name, description, status, created_by, created_date)
                VALUES (?, ?, ?, 'Active', ?, datetime('now'))
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(projectSql)) {
                pstmt.setInt(1, teamId);
                pstmt.setString(2, job.getTitle() + " - Client: " + clientName);
                pstmt.setString(3, "Client Project: " + job.getDescription() + 
                                  "\n\nBudget: " + job.getBudget() + 
                                  "\nDuration: " + (job.getDuration() != null ? job.getDuration() : "Not specified"));
                pstmt.setInt(4, job.getClientId());
                int projectRows = pstmt.executeUpdate();
                System.out.println("Project created: " + projectRows + " rows");
            }
            
            // Update job status to In Progress using the same connection
            String updateJobSql = "UPDATE jobs SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateJobSql)) {
                pstmt.setString(1, "In Progress");
                pstmt.setInt(2, jobId);
                int statusRows = pstmt.executeUpdate();
                System.out.println("Job status updated to In Progress: " + statusRows + " rows");
            }
            
            // Get team members using the same connection
            System.out.println("Fetching team members for teamId: " + teamId);
            java.util.List<User> teamMembers = new java.util.ArrayList<>();
            String membersSql = """
                SELECT u.* FROM users u
                INNER JOIN team_members tm ON u.id = tm.user_id
                WHERE tm.team_id = ?
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(membersSql)) {
                pstmt.setInt(1, teamId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    teamMembers.add(user);
                }
            }
            System.out.println("Found " + teamMembers.size() + " team members");
            
            // Notify all team members using the same connection
            String notifSql = "INSERT INTO notifications (user_id, type, message, related_id, is_read, created_date) VALUES (?, ?, ?, ?, ?, ?)";
            for (User member : teamMembers) {
                String message = "New project assigned! Your team has been approved for: " + job.getTitle();
                try (PreparedStatement pstmt = conn.prepareStatement(notifSql)) {
                    pstmt.setInt(1, member.getId());
                    pstmt.setString(2, "project_assigned");
                    pstmt.setString(3, message);
                    pstmt.setString(4, String.valueOf(teamId));
                    pstmt.setBoolean(5, false);
                    pstmt.setString(6, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    pstmt.executeUpdate();
                    System.out.println("Notification sent to user " + member.getId());
                }
            }
            
            conn.commit();
            System.out.println("Transaction committed successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("SQL Error approving application: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error approving application: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("Connection closed");
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Reject job application
     */
    public static boolean rejectApplication(int applicationId) {
        String sql = "UPDATE job_applications SET status = 'Rejected', response_date = datetime('now') WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) {
            System.err.println("Database connection is null");
            return false;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, applicationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting application: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
