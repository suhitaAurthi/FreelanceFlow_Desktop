package com.example.freelanceflow_desktop;

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
                try {
                    // Try to load the SQLite JDBC driver
                    Class.forName("org.sqlite.JDBC");
                    System.out.println("SQLite JDBC Driver loaded successfully!");
                } catch (ClassNotFoundException e) {
                    System.err.println("SQLite JDBC Driver not found!");
                    System.err.println("Make sure sqlite-jdbc dependency is in pom.xml");
                    e.printStackTrace();
                    
                    // Try without explicit driver loading (JDBC 4.0+)
                    System.out.println("Attempting connection without explicit driver loading...");
                }
                
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Database connection established!");
                System.out.println("Database URL: " + DB_URL);
                initializeDatabase();
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
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

            stmt.execute(createUsersTable);
            stmt.execute(createFreelancerTable);
            stmt.execute(createClientTable);

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
