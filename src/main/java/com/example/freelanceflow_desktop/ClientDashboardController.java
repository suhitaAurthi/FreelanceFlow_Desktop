package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private VBox postJobView;
    @FXML private VBox myJobsView;
    @FXML private VBox teamsView;
    @FXML private VBox applicationsView;
    
    // Post Job fields
    @FXML private TextField jobTitleField;
    @FXML private TextArea jobDescriptionArea;
    @FXML private TextField jobBudgetField;
    @FXML private TextField jobDurationField;
    @FXML private TextField jobSkillsField;
    
    // Lists
    @FXML private VBox myJobsList;
    @FXML private VBox teamsList;
    @FXML private VBox applicationsList;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + "!");
        }
        
        // Load initial data
        loadMyJobs();
        loadTeams();
    }

    @FXML
    protected void showPostJobView() {
        hideAllViews();
        postJobView.setVisible(true);
    }

    @FXML
    protected void showMyJobsView() {
        hideAllViews();
        myJobsView.setVisible(true);
        loadMyJobs();
    }

    @FXML
    protected void showTeamsView() {
        hideAllViews();
        teamsView.setVisible(true);
        loadTeams();
    }

    @FXML
    protected void showApplicationsView() {
        hideAllViews();
        applicationsView.setVisible(true);
        loadApplications();
    }

    private void hideAllViews() {
        postJobView.setVisible(false);
        myJobsView.setVisible(false);
        teamsView.setVisible(false);
        applicationsView.setVisible(false);
    }

    @FXML
    protected void onPostJob() {
        String title = jobTitleField.getText().trim();
        String description = jobDescriptionArea.getText().trim();
        String budget = jobBudgetField.getText().trim();
        String duration = jobDurationField.getText().trim();
        String skills = jobSkillsField.getText().trim();

        if (title.isEmpty() || description.isEmpty() || budget.isEmpty()) {
            showAlert("Error", "Please fill in all required fields!", Alert.AlertType.ERROR);
            return;
        }

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user session found!", Alert.AlertType.ERROR);
            return;
        }

        // TODO: Save job to database
        showAlert("Success", "Job posted successfully!", Alert.AlertType.INFORMATION);
        
        // Clear fields
        jobTitleField.clear();
        jobDescriptionArea.clear();
        jobBudgetField.clear();
        jobDurationField.clear();
        jobSkillsField.clear();
        
        // Refresh job list
        loadMyJobs();
    }

    private void loadMyJobs() {
        myJobsList.getChildren().clear();
        
        // TODO: Load jobs from database
        Label placeholder = new Label("No jobs posted yet. Click 'Post Job' to create one!");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        myJobsList.getChildren().add(placeholder);
    }

    private void loadTeams() {
        teamsList.getChildren().clear();
        
        // TODO: Load teams from database
        Label placeholder = new Label("No teams available yet.");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        teamsList.getChildren().add(placeholder);
    }

    private void loadApplications() {
        applicationsList.getChildren().clear();
        
        // TODO: Load applications from database
        Label placeholder = new Label("No applications yet.");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        applicationsList.getChildren().add(placeholder);
    }

    @FXML
    protected void onLogout() {
        SessionManager.clearSession();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomePage.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to logout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
