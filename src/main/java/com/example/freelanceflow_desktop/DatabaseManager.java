package com.example.freelanceflow_desktop;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:freelanceflow.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("=== DATABASE CONNECTION ATTEMPT ===");
                
                // Get absolute path for database file
                File dbFile = new File("freelanceflow.db");
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

            stmt.execute(createUsersTable);
            stmt.execute(createFreelancerTable);
            stmt.execute(createClientTable);
            stmt.execute(createTeamsTable);
            stmt.execute(createTeamMembersTable);
            stmt.execute(createNotificationsTable);

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
