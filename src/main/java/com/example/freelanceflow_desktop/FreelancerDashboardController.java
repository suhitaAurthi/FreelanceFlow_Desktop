package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class FreelancerDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label teamsCountLabel;
    @FXML private Label jobsCountLabel;
    @FXML private Label recruitmentsCountLabel;
    @FXML private Button myTeamsBtn;
    @FXML private Button createTeamBtn;
    @FXML private Button browseTeamsBtn;
    @FXML private Button recruitmentsBtn;
    @FXML private Button jobsBtn;
    @FXML private StackPane contentArea;
    @FXML private VBox myTeamsView;
    @FXML private VBox createTeamView;
    @FXML private VBox browseTeamsView;
    @FXML private VBox recruitmentsView;
    @FXML private VBox jobsView;
    
    // Create Team fields
    @FXML private TextField teamNameField;
    @FXML private TextArea teamDescriptionArea;
    @FXML private TextField teamSkillsField;
    @FXML private TextField teamMaxMembersField;
    
    // Lists
    @FXML private VBox myTeamsList;
    @FXML private VBox browseTeamsList;
    @FXML private VBox recruitmentsList;
    @FXML private VBox jobsList;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + "!");
        }
        
        // Update navigation button styles
        updateNavigationButtons(myTeamsBtn);
        
        // Load initial data and update counts
        loadMyTeams();
        updateDashboardCounts();
    }
    
    private void updateDashboardCounts() {
        // TODO: Get actual counts from database
        teamsCountLabel.setText("0");
        jobsCountLabel.setText("0");
        recruitmentsCountLabel.setText("0");
    }
    
    private void updateNavigationButtons(Button activeButton) {
        // Reset all buttons
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String activeStyle = "-fx-background-color: #2B8994; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        
        myTeamsBtn.setStyle(inactiveStyle);
        createTeamBtn.setStyle(inactiveStyle);
        browseTeamsBtn.setStyle(inactiveStyle);
        recruitmentsBtn.setStyle(inactiveStyle);
        jobsBtn.setStyle(inactiveStyle);
        
        // Highlight active button
        activeButton.setStyle(activeStyle);
    }

    @FXML
    protected void showMyTeamsView() {
        hideAllViews();
        myTeamsView.setVisible(true);
        updateNavigationButtons(myTeamsBtn);
        loadMyTeams();
    }

    @FXML
    protected void showCreateTeamView() {
        hideAllViews();
        createTeamView.setVisible(true);
        updateNavigationButtons(createTeamBtn);
    }

    @FXML
    protected void showBrowseTeamsView() {
        hideAllViews();
        browseTeamsView.setVisible(true);
        updateNavigationButtons(browseTeamsBtn);
        loadAvailableTeams();
    }

    @FXML
    protected void showRecruitmentsView() {
        hideAllViews();
        recruitmentsView.setVisible(true);
        updateNavigationButtons(recruitmentsBtn);
        loadRecruitments();
    }

    @FXML
    protected void showJobsView() {
        hideAllViews();
        jobsView.setVisible(true);
        updateNavigationButtons(jobsBtn);
        loadJobs();
    }

    private void hideAllViews() {
        myTeamsView.setVisible(false);
        createTeamView.setVisible(false);
        browseTeamsView.setVisible(false);
        recruitmentsView.setVisible(false);
        jobsView.setVisible(false);
    }

    @FXML
    protected void onCreateTeam() {
        String name = teamNameField.getText().trim();
        String description = teamDescriptionArea.getText().trim();
        String skills = teamSkillsField.getText().trim();
        String maxMembers = teamMaxMembersField.getText().trim();

        if (name.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Please fill in all required fields!", Alert.AlertType.ERROR);
            return;
        }

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user session found!", Alert.AlertType.ERROR);
            return;
        }

        // TODO: Save team to database with current user as admin
        showAlert("Success", "Team created successfully! You are now the team admin.", Alert.AlertType.INFORMATION);
        
        // Clear fields
        teamNameField.clear();
        teamDescriptionArea.clear();
        teamSkillsField.clear();
        teamMaxMembersField.clear();
        
        // Refresh team list
        loadMyTeams();
        showMyTeamsView();
    }

    private void loadMyTeams() {
        myTeamsList.getChildren().clear();
        
        // TODO: Load user's teams from database
        VBox emptyState = createEmptyState(
            "No Teams Yet",
            "You haven't joined any teams yet. Create a new team or browse existing ones!",
            "Create Team",
            this::showCreateTeamView
        );
        myTeamsList.getChildren().add(emptyState);
    }

    private void loadAvailableTeams() {
        browseTeamsList.getChildren().clear();
        
        // TODO: Load available teams from database
        VBox emptyState = createEmptyState(
            "No Teams Available",
            "There are no teams available to join at the moment. Be the first to create one!",
            null,
            null
        );
        browseTeamsList.getChildren().add(emptyState);
    }

    private void loadRecruitments() {
        recruitmentsList.getChildren().clear();
        
        // TODO: Load recruitment posts from database
        VBox emptyState = createEmptyState(
            "No Recruitment Posts",
            "There are no recruitment posts available yet. Check back later!",
            null,
            null
        );
        recruitmentsList.getChildren().add(emptyState);
    }

    private void loadJobs() {
        jobsList.getChildren().clear();
        
        // TODO: Load jobs from database
        VBox emptyState = createEmptyState(
            "No Jobs Available",
            "There are no job postings yet. Check back soon for new opportunities!",
            null,
            null
        );
        jobsList.getChildren().add(emptyState);
    }
    
    private VBox createEmptyState(String title, String message, String buttonText, Runnable action) {
        VBox container = new VBox(15);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.setStyle("-fx-background-color: white; -fx-padding: 60; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        
        container.getChildren().addAll(titleLabel, messageLabel);
        
        if (buttonText != null && action != null) {
            Button actionButton = new Button(buttonText);
            actionButton.setStyle("-fx-background-color: #2B8994; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 20; -fx-cursor: hand;");
            actionButton.setOnAction(e -> action.run());
            container.getChildren().add(actionButton);
        }
        
        return container;
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
