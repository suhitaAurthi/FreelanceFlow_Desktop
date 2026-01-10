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
        String password = passwordField.getText();

        // Validate fields
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both email and password!", AlertType.ERROR);
            return;
        }

        // Authenticate user
        User user = UserDAO.getUserByEmail(email);
        
        if (user == null) {
            showAlert("Error", "Email not found! Please sign up first.", AlertType.ERROR);
            return;
        }
        
        if (!user.getPassword().equals(password)) {
            showAlert("Error", "Incorrect password!", AlertType.ERROR);
            return;
        }
        
        // Set current user in session
        SessionManager.setCurrentUser(user);
        
        // Navigate to welcome message with user's role
        navigateToWelcomeMessage(user.getRole());
    }

    private void navigateToWelcomeMessage(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeMessage.fxml"));
            Parent root = loader.load();
            
            WelcomeMessageController controller = loader.getController();
            controller.setUserRole(role);
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load welcome page: " + e.getMessage(), AlertType.ERROR);
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
