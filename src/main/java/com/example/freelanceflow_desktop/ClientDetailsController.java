package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ClientDetailsController {
    @FXML
    private ImageView profileImageView;
    
    @FXML
    private TextField displayNameField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField locationField;
    
    @FXML
    private TextField budgetField;
    
    private File selectedImageFile;

    @FXML
    protected void onUploadLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Company Logo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) profileImageView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    @FXML
    protected void onSaveAndContinue() {
        String displayName = displayNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String location = locationField.getText().trim();
        String budget = budgetField.getText().trim();

        // Validate fields
        if (displayName.isEmpty() || phone.isEmpty() || location.isEmpty() || budget.isEmpty()) {
            showAlert("Error", "All fields are required!", AlertType.ERROR);
            return;
        }

        // Get current user from session
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user session found!", AlertType.ERROR);
            return;
        }

        // Save to database
        String logoPath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;
        boolean saved = ClientDetailsDAO.saveClientDetails(
            currentUser.getId(), displayName, phone, location, budget, logoPath
        );

        if (saved) {
            System.out.println("Client details saved to database successfully!");
        } else {
            System.out.println("Warning: Failed to save to database, but continuing to dashboard...");
        }
        
        // Navigate to dashboard regardless of database save status
        navigateToClientDashboard();
    }

    private void navigateToClientDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientDashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) displayNameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load dashboard: " + e.getMessage(), AlertType.ERROR);
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
