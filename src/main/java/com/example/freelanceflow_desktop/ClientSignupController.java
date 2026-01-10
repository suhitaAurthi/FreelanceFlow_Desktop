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

public class ClientSignupController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onCreateAccount() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required!", AlertType.ERROR);
            return;
        }

        if (password.length() < 8) {
            showAlert("Error", "Password must be 8 or more characters!", AlertType.ERROR);
            return;
        }

        // Check if email already exists
        if (UserDAO.emailExists(email)) {
            showAlert("Error", "Email already registered! Please use a different email.", AlertType.ERROR);
            return;
        }

        // Save user to database
        User user = new User(firstName, lastName, email, password, "client");
        int userId = UserDAO.createUser(user);
        
        if (userId > 0) {
            // Set current user in session
            SessionManager.setCurrentUser(user);
            
            // Navigate to welcome message
            navigateToWelcomeMessage("client");
        } else {
            showAlert("Error", "Failed to create account. Please try again.", AlertType.ERROR);
        }
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
