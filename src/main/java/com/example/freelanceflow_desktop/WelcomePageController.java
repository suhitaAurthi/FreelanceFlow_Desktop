package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomePageController {
    @FXML
    private Button clientButton;
    
    @FXML
    private Button freelancerButton;
    
    @FXML
    private Button createAccountButton;
    
    private String selectedRole = null;
    
    private static final String SELECTED_STYLE = "-fx-background-color: #2B8994; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10;";
    
    private static final String UNSELECTED_STYLE = "-fx-background-color: #E0E0E0; " +
            "-fx-text-fill: #555555; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10;";

    @FXML
    protected void onClientSelected() {
        selectedRole = "client";
        clientButton.setStyle(SELECTED_STYLE);
        freelancerButton.setStyle(UNSELECTED_STYLE);
    }

    @FXML
    protected void onFreelancerSelected() {
        selectedRole = "freelancer";
        freelancerButton.setStyle(SELECTED_STYLE);
        clientButton.setStyle(UNSELECTED_STYLE);
    }

    @FXML
    protected void onCreateAccount() {
        if (selectedRole == null) {
            showAlert("Error", "Please select your role first!", AlertType.ERROR);
            return;
        }

        try {
            String fxmlFile = selectedRole.equals("freelancer") 
                ? "FreelancerSignup.fxml" 
                : "ClientSignup.fxml";
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load signup page: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    protected void onLoginClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login page: " + e.getMessage(), AlertType.ERROR);
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
