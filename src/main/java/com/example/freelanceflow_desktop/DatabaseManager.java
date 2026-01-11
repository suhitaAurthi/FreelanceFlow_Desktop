package com.example.freelanceflow_desktop;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Use absolute path in user's home directory to ensure consistent database location
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".freelanceflow";
    private static final String DB_FILE = DB_DIR + File.separator + "freelanceflow.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("=== DATABASE CONNECTION ATTEMPT ===");
                
                // Create database directory if it doesn't exist
                File dbDirectory = new File(DB_DIR);
                if (!dbDirectory.exists()) {
                    boolean created = dbDirectory.mkdirs();
                    System.out.println("Database directory created: " + created);
                }
                
                // Get absolute path for database file
                File dbFile = new File(DB_FILE);
                System.out.println("Database file path: " + dbFile.getAbsolutePath());
                System.out.println("Database file exists: " + dbFile.exists());
                
                try {
                    // Try to load the SQLite JDBC driver
                    Class.forName("org.sqlite.JDBC");
                    System.out.println("✓ SQLite JDBC Driver loaded successfully!");
                } catch (ClassNotFoundException e) {
                    System.err.println("✗ SQLite JDBC Driver not found!");
                    System.err.println("Make sure sqlite-jdbc dependency is in pom.xml and project is rebuilt");
                    e.printStackTrace();
                }
                
                System.out.println("Attempting to connect to: " + DB_URL);
                connection = DriverManager.getConnection(DB_URL);
                
                if (connection != null) {
                    System.out.println("✓ Database connection established successfully!");
                    System.out.println("✓ Connection is valid: " + connection.isValid(5));
                    initializeDatabase();
                } else {
                    System.err.println("✗ Connection returned null!");
                }
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("✗ Error connecting to database: " + e.getMessage());
            System.err.println("Database URL attempted: " + DB_URL);
            e.printStackTrace();
            return null;
        }
    }

    private static void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;

            // Create freelancer_details table
            String createFreelancerTable = """
                CREATE TABLE IF NOT EXISTS freelancer_details (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    display_name TEXT,
                    phone TEXT,
                    location TEXT,
                    rate_per_hour TEXT,
                    profile_photo_path TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """;

            // Create client_details table
            String createClientTable = """
                CREATE TABLE IF NOT EXISTS client_details (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    display_name TEXT,
                    phone TEXT,
                    location TEXT,
                    project_budget TEXT,
                    company_logo_path TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """;
            
            // Create teams table
            String createTeamsTable = """
                CREATE TABLE IF NOT EXISTS teams (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    skills TEXT,
                    max_members INTEGER,
                    admin_id INTEGER NOT NULL,
                    created_date TEXT,
                    FOREIGN KEY (admin_id) REFERENCES users(id)
                )
            """;
            
            // Create team_members table
            String createTeamMembersTable = """
                CREATE TABLE IF NOT EXISTS team_members (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    role TEXT NOT NULL,
                    joined_date TEXT,
                    FOREIGN KEY (team_id) REFERENCES teams(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """;
            
            // Create notifications table
            String createNotificationsTable = """
                CREATE TABLE IF NOT EXISTS notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    message TEXT NOT NULL,
                    related_id TEXT,
                    is_read INTEGER DEFAULT 0,
                    created_date TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """;
            
            // Create projects table
            String createProjectsTable = """
                CREATE TABLE IF NOT EXISTS projects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    status TEXT DEFAULT 'Active',
                    start_date TEXT,
                    end_date TEXT,
                    created_by INTEGER NOT NULL,
                    created_date TEXT,
                    FOREIGN KEY (team_id) REFERENCES teams(id),
                    FOREIGN KEY (created_by) REFERENCES users(id)
                )
            """;
            
            // Create recruitment_posts table
            String createRecruitmentPostsTable = """
                CREATE TABLE IF NOT EXISTS recruitment_posts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    required_skills TEXT,
                    positions INTEGER DEFAULT 1,
                    status TEXT DEFAULT 'Open',
                    created_by INTEGER NOT NULL,
                    created_date TEXT,
                    FOREIGN KEY (team_id) REFERENCES teams(id),
                    FOREIGN KEY (created_by) REFERENCES users(id)
                )
            """;
            
            // Create team_invitations table
            String createTeamInvitationsTable = """
                CREATE TABLE IF NOT EXISTS team_invitations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    invited_by INTEGER NOT NULL,
                    status TEXT DEFAULT 'Pending',
                    invited_date TEXT,
                    response_date TEXT,
                    FOREIGN KEY (team_id) REFERENCES teams(id),
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (invited_by) REFERENCES users(id)
                )
            """;
            
            // Create jobs table
            String createJobsTable = """
                CREATE TABLE IF NOT EXISTS jobs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    client_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    budget TEXT,
                    duration TEXT,
                    skills TEXT,
                    status TEXT DEFAULT 'Open',
                    posted_date TEXT,
                    FOREIGN KEY (client_id) REFERENCES users(id)
                )
            """;
            
            // Create service_posts table
            String createServicePostsTable = """
                CREATE TABLE IF NOT EXISTS service_posts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    category TEXT,
                    pricing TEXT,
                    delivery_time TEXT,
                    status TEXT DEFAULT 'Active',
                    created_by INTEGER NOT NULL,
                    created_date TEXT,
                    FOREIGN KEY (team_id) REFERENCES teams(id),
                    FOREIGN KEY (created_by) REFERENCES users(id)
                )
            """;
            
            // Create job_applications table (when teams apply for client jobs)
            String createJobApplicationsTable = """
                CREATE TABLE IF NOT EXISTS job_applications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    job_id INTEGER NOT NULL,
                    team_id INTEGER NOT NULL,
                    status TEXT DEFAULT 'Pending',
                    applied_date TEXT,
                    response_date TEXT,
                    FOREIGN KEY (job_id) REFERENCES jobs(id),
                    FOREIGN KEY (team_id) REFERENCES teams(id)
                )
            """;
            
            // Create service_requests table (when clients request team services)
            String createServiceRequestsTable = """
                CREATE TABLE IF NOT EXISTS service_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    service_id INTEGER NOT NULL,
                    client_id INTEGER NOT NULL,
                    status TEXT DEFAULT 'Pending',
                    request_date TEXT,
                    response_date TEXT,
                    FOREIGN KEY (service_id) REFERENCES service_posts(id),
                    FOREIGN KEY (client_id) REFERENCES users(id)
                )
            """;

            stmt.execute(createUsersTable);
            stmt.execute(createFreelancerTable);
            stmt.execute(createClientTable);
            stmt.execute(createTeamsTable);
            stmt.execute(createTeamMembersTable);
            stmt.execute(createNotificationsTable);
            stmt.execute(createProjectsTable);
            stmt.execute(createRecruitmentPostsTable);
            stmt.execute(createTeamInvitationsTable);
            stmt.execute(createJobsTable);
            stmt.execute(createServicePostsTable);
            stmt.execute(createJobApplicationsTable);
            stmt.execute(createServiceRequestsTable);

            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
