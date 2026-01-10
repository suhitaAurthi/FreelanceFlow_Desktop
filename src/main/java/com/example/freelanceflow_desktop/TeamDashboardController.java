package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class TeamDashboardController {
    @FXML private Label teamNameLabel;
    @FXML private Label roleLabel;
    @FXML private Label membersCountLabel;
    @FXML private Label projectsCountLabel;
    @FXML private Label tasksCountLabel;
    
    @FXML private Button overviewBtn;
    @FXML private Button membersBtn;
    @FXML private Button projectsBtn;
    @FXML private Button recruitmentBtn;
    @FXML private Button settingsBtn;
    @FXML private Button inviteMemberBtn;
    @FXML private Button createProjectBtn;
    @FXML private Button createPostBtn;
    
    @FXML private StackPane contentArea;
    @FXML private VBox overviewView;
    @FXML private VBox membersView;
    @FXML private VBox projectsView;
    @FXML private VBox recruitmentView;
    @FXML private VBox settingsView;
    
    // Overview elements
    @FXML private Label overviewTeamName;
    @FXML private Label teamDescription;
    @FXML private Label teamSkills;
    @FXML private VBox activityFeed;
    
    // Lists
    @FXML private VBox membersList;
    @FXML private VBox projectsList;
    @FXML private VBox recruitmentList;
    
    // Settings
    @FXML private TextField settingsTeamName;
    @FXML private TextArea settingsTeamDescription;
    
    private String teamId;
    private String userRole;

    @FXML
    public void initialize() {
        // TODO: Load team data from database
        teamNameLabel.setText("My Awesome Team");
        roleLabel.setText("Admin");
        
        updateNavigationButtons(overviewBtn);
        loadTeamData();
        updateDashboardCounts();
    }
    
    public void setTeamData(String teamId, String teamName, String role) {
        this.teamId = teamId;
        this.userRole = role;
        teamNameLabel.setText(teamName);
        roleLabel.setText(role);
        
        // Hide admin-only buttons if user is not admin
        if (!"Admin".equals(role)) {
            inviteMemberBtn.setVisible(false);
            settingsBtn.setVisible(false);
        }
    }
    
    private void updateDashboardCounts() {
        // TODO: Get actual counts from database
        membersCountLabel.setText("0");
        projectsCountLabel.setText("0");
        tasksCountLabel.setText("0");
    }
    
    private void loadTeamData() {
        // TODO: Load from database
        overviewTeamName.setText("My Awesome Team");
        teamDescription.setText("A team dedicated to building innovative web applications using modern technologies.");
        teamSkills.setText("Java, JavaScript, React, Node.js, UI/UX Design");
        
        loadActivityFeed();
        loadMembers();
        loadProjects();
        loadRecruitmentPosts();
    }
    
    private void loadActivityFeed() {
        activityFeed.getChildren().clear();
        
        Label placeholder = new Label("No recent activity");
        placeholder.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");
        activityFeed.getChildren().add(placeholder);
    }
    
    private void loadMembers() {
        membersList.getChildren().clear();
        
        if (teamId == null) {
            return;
        }
        
        int teamIdInt = Integer.parseInt(teamId);
        
        // Load actual members from database
        List<User> members = TeamDAO.getTeamMembers(teamIdInt);
        
        if (members.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Members Yet",
                "Invite team members to start collaborating on projects!",
                "Admin".equals(userRole) ? "Invite Member" : null,
                "Admin".equals(userRole) ? this::onInviteMember : null
            );
            membersList.getChildren().add(emptyState);
        } else {
            for (User member : members) {
                String memberRole = TeamDAO.getUserTeamRole(teamIdInt, member.getId());
                VBox memberCard = createMemberCard(member, memberRole);
                membersList.getChildren().add(memberCard);
            }
        }
    }
    
    private VBox createMemberCard(User member, String role) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(member.getFirstName() + " " + member.getLastName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label roleLabel = new Label(role);
        String roleColor = "Admin".equals(role) ? "#E74C3C" : "#2B8994";
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: " + roleColor + "; -fx-padding: 5 15; -fx-background-radius: 10;");
        
        header.getChildren().addAll(nameLabel, roleLabel);
        
        Label emailLabel = new Label(member.getEmail());
        emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        card.getChildren().addAll(header, emailLabel);
        
        // Only show remove button for admin and if not removing themselves
        User currentUser = SessionManager.getCurrentUser();
        if ("Admin".equals(userRole) && currentUser.getId() != member.getId()) {
            Button removeBtn = new Button("Remove");
            removeBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 15; -fx-background-radius: 15; -fx-cursor: hand;");
            removeBtn.setOnAction(e -> removeMember(member.getId()));
            card.getChildren().add(removeBtn);
        }
        
        return card;
    }
    
    private void removeMember(int userId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Member");
        confirm.setHeaderText("Are you sure you want to remove this member?");
        confirm.setContentText("This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int teamIdInt = Integer.parseInt(teamId);
                boolean removed = TeamDAO.removeMember(teamIdInt, userId);
                
                if (removed) {
                    showAlert("Success", "Member removed successfully!", Alert.AlertType.INFORMATION);
                    loadMembers();
                } else {
                    showAlert("Error", "Failed to remove member.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void loadProjects() {
        projectsList.getChildren().clear();
        
        VBox emptyState = createEmptyState(
            "No Projects Yet",
            "Create your first project to organize your team's work!",
            "Create Project",
            this::onCreateProject
        );
        projectsList.getChildren().add(emptyState);
    }
    
    private void loadRecruitmentPosts() {
        recruitmentList.getChildren().clear();
        
        VBox emptyState = createEmptyState(
            "No Recruitment Posts",
            "Post a recruitment to find new team members!",
            "Post Recruitment",
            this::onCreateRecruitmentPost
        );
        recruitmentList.getChildren().add(emptyState);
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
    
    private void updateNavigationButtons(Button activeButton) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String activeStyle = "-fx-background-color: #2B8994; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        
        overviewBtn.setStyle(inactiveStyle);
        membersBtn.setStyle(inactiveStyle);
        projectsBtn.setStyle(inactiveStyle);
        recruitmentBtn.setStyle(inactiveStyle);
        settingsBtn.setStyle(inactiveStyle);
        
        activeButton.setStyle(activeStyle);
    }
    
    private void hideAllViews() {
        overviewView.setVisible(false);
        membersView.setVisible(false);
        projectsView.setVisible(false);
        recruitmentView.setVisible(false);
        settingsView.setVisible(false);
    }
    
    @FXML
    protected void showOverviewView() {
        hideAllViews();
        overviewView.setVisible(true);
        updateNavigationButtons(overviewBtn);
    }
    
    @FXML
    protected void showMembersView() {
        hideAllViews();
        membersView.setVisible(true);
        updateNavigationButtons(membersBtn);
        loadMembers();
    }
    
    @FXML
    protected void showProjectsView() {
        hideAllViews();
        projectsView.setVisible(true);
        updateNavigationButtons(projectsBtn);
        loadProjects();
    }
    
    @FXML
    protected void showRecruitmentView() {
        hideAllViews();
        recruitmentView.setVisible(true);
        updateNavigationButtons(recruitmentBtn);
        loadRecruitmentPosts();
    }
    
    @FXML
    protected void showSettingsView() {
        hideAllViews();
        settingsView.setVisible(true);
        updateNavigationButtons(settingsBtn);
        
        // Load current settings
        settingsTeamName.setText(teamNameLabel.getText());
        settingsTeamDescription.setText(teamDescription.getText());
    }
    
    @FXML
    protected void onInviteMember() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Invite Member");
        dialog.setHeaderText("Invite a member to join your team");
        dialog.setContentText("Enter member's email:");
        
        dialog.showAndWait().ifPresent(email -> {
            if (email.trim().isEmpty()) {
                showAlert("Error", "Please enter an email address!", Alert.AlertType.ERROR);
                return;
            }
            
            // Check if user exists
            User invitedUser = UserDAO.getUserByEmail(email.trim());
            
            if (invitedUser == null) {
                showAlert("Error", "User with this email not found!", Alert.AlertType.ERROR);
                return;
            }
            
            // Check if user is a freelancer
            if (!invitedUser.getRole().equalsIgnoreCase("freelancer")) {
                showAlert("Error", "Only freelancers can be invited to teams!", Alert.AlertType.ERROR);
                return;
            }
            
            // Check if user is already in the team
            int teamIdInt = Integer.parseInt(teamId);
            if (TeamDAO.isUserInTeam(teamIdInt, invitedUser.getId())) {
                showAlert("Error", "This user is already a member of the team!", Alert.AlertType.ERROR);
                return;
            }
            
            // Add member to team
            boolean added = TeamDAO.addTeamMember(teamIdInt, invitedUser.getId(), "Member");
            
            if (added) {
                // Create notification for invited user
                Team team = TeamDAO.getTeamById(teamIdInt);
                String notificationMessage = "You have been invited to join the team: " + team.getName();
                NotificationDAO.createNotification(invitedUser.getId(), "TEAM_INVITE", notificationMessage, teamId);
                
                showAlert("Success", invitedUser.getFirstName() + " has been added to the team!", Alert.AlertType.INFORMATION);
                loadMembers();
            } else {
                showAlert("Error", "Failed to add member. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }
    
    @FXML
    protected void onCreateProject() {
        showAlert("Coming Soon", "Project creation feature will be available soon!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    protected void onCreateRecruitmentPost() {
        showAlert("Coming Soon", "Recruitment posting feature will be available soon!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    protected void onSaveSettings() {
        String newName = settingsTeamName.getText().trim();
        String newDescription = settingsTeamDescription.getText().trim();
        
        if (newName.isEmpty() || newDescription.isEmpty()) {
            showAlert("Error", "Please fill in all fields!", Alert.AlertType.ERROR);
            return;
        }
        
        // TODO: Save to database
        teamNameLabel.setText(newName);
        overviewTeamName.setText(newName);
        teamDescription.setText(newDescription);
        
        showAlert("Success", "Team settings updated successfully!", Alert.AlertType.INFORMATION);
        showOverviewView();
    }
    
    @FXML
    protected void onLeaveTeam() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Leave Team");
        confirm.setHeaderText("Are you sure you want to leave this team?");
        confirm.setContentText("This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Remove user from team in database
                onBackToDashboard();
            }
        });
    }
    
    @FXML
    protected void onBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FreelancerDashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) teamNameLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to go back: " + e.getMessage(), Alert.AlertType.ERROR);
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
