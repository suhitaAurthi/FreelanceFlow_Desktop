package com.example.freelanceflow_desktop;

import javafx.application.Application;
import java.sql.Connection;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("=== FREELANCEFLOW APPLICATION STARTING ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        
        // Initialize database connection on startup
        System.out.println("\n=== INITIALIZING DATABASE ===");
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            System.out.println("✓ Database initialized successfully on startup\n");
        } else {
            System.err.println("✗ WARNING: Database connection failed on startup");
            System.err.println("The application will continue but database features may not work\n");
        }
        
        System.out.println("=== LAUNCHING UI ===");
        Application.launch(WelcomePage.class, args);
    }
}
