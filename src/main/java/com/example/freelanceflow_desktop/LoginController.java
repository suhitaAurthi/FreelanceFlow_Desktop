package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email entered: '" + email + "'");
        System.out.println("Password entered: '" + password + "' (length: " + password.length() + ")");

        // Validate fields
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both email and password!", AlertType.ERROR);
            return;
        }

        // Authenticate user
        User user = UserDAO.getUserByEmail(email);
        
        System.out.println("Database lookup result: " + (user != null ? "User found" : "User NOT found"));
        
        if (user == null) {
            showAlert("Error", "Email not found! Please sign up first.", AlertType.ERROR);
            return;
        }
        
        System.out.println("User details - Email: " + user.getEmail() + ", Role: " + user.getRole());
        
        // Trim stored password as well for comparison
        String storedPassword = user.getPassword().trim();
        
        System.out.println("Stored password: '" + storedPassword + "' (length: " + storedPassword.length() + ")");
        System.out.println("Passwords match: " + storedPassword.equals(password));
        
        if (!storedPassword.equals(password)) {
            showAlert("Error", "Incorrect password!", AlertType.ERROR);
            return;
        }
        
        System.out.println("Login successful! Redirecting...");
        
        // Set current user in session
        SessionManager.setCurrentUser(user);
        
        // Check if freelancer is in a team, redirect to team dashboard
        if (user.getRole().equalsIgnoreCase("freelancer")) {
            Team userTeam = TeamDAO.getUserFirstTeam(user.getId());
            if (userTeam != null) {
                String role = TeamDAO.getUserTeamRole(userTeam.getId(), user.getId());
                navigateToTeamDashboard(userTeam, role);
                return;
            }
        }
        
        // Navigate to appropriate dashboard based on role
        navigateToDashboard(user.getRole());
    }

    private void navigateToDashboard(String role) {
        try {
            String fxmlFile = role.equalsIgnoreCase("freelancer") 
                ? "FreelancerDashboard.fxml" 
                : "ClientDashboard.fxml";
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load dashboard: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    private void navigateToTeamDashboard(Team team, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamDashboard.fxml"));
            Parent root = loader.load();
            
            TeamDashboardController controller = loader.getController();
            controller.setTeamData(String.valueOf(team.getId()), team.getName(), role);
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load team dashboard: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
