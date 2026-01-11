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
    @FXML private Button jobsBtn;
    @FXML private Button servicesBtn;
    @FXML private Button settingsBtn;
    @FXML private Button inviteMemberBtn;
    @FXML private Button createProjectBtn;
    @FXML private Button createPostBtn;
    @FXML private Button createServiceBtn;
    
    @FXML private StackPane contentArea;
    @FXML private VBox overviewView;
    @FXML private VBox membersView;
    @FXML private VBox projectsView;
    @FXML private VBox recruitmentView;
    @FXML private VBox jobsView;
    @FXML private VBox servicesView;
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
    @FXML private VBox jobsList;
    @FXML private VBox servicesList;
    
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
        
        // Reload data after team is set
        loadTeamData();
        updateDashboardCounts();
    }
    
    private void updateDashboardCounts() {
        if (teamId == null) {
            membersCountLabel.setText("0");
            projectsCountLabel.setText("0");
            tasksCountLabel.setText("0");
            return;
        }
        
        int teamIdInt = Integer.parseInt(teamId);
        
        // Get actual counts from database
        int memberCount = TeamDAO.getTeamMembers(teamIdInt).size();
        int activeProjectCount = ProjectDAO.getActiveProjectCount(teamIdInt);
        int completedProjectCount = ProjectDAO.getCompletedProjectCount(teamIdInt);
        
        membersCountLabel.setText(String.valueOf(memberCount));
        projectsCountLabel.setText(String.valueOf(activeProjectCount));
        tasksCountLabel.setText(String.valueOf(completedProjectCount));
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
        
        // Profile photo
        javafx.scene.image.ImageView profileImg = new javafx.scene.image.ImageView();
        profileImg.setFitHeight(50);
        profileImg.setFitWidth(50);
        profileImg.setPreserveRatio(true);
        
        // Load freelancer photo
        try {
            String photoPath = FreelancerDetailsDAO.getProfilePhotoPath(member.getId());
            if (photoPath != null && !photoPath.isEmpty()) {
                java.io.File photoFile = new java.io.File(photoPath);
                if (photoFile.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(photoFile)) {
                        javafx.scene.image.Image image = new javafx.scene.image.Image(fis);
                        profileImg.setImage(image);
                        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(25, 25, 25);
                        profileImg.setClip(clip);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading member photo: " + e.getMessage());
        }
        
        VBox nameBox = new VBox(3);
        Label nameLabel = new Label(member.getFirstName() + " " + member.getLastName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label roleLabel = new Label(role);
        String roleColor = "Admin".equals(role) ? "#E74C3C" : "#1F2937";
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: " + roleColor + "; -fx-padding: 5 15; -fx-background-radius: 10;");
        
        nameBox.getChildren().addAll(nameLabel, roleLabel);
        
        header.getChildren().addAll(profileImg, nameBox);
        
        Label emailLabel = new Label("📧 " + member.getEmail());
        emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        card.getChildren().addAll(header, emailLabel);
        
        // Get freelancer details from database
        try (java.sql.Connection conn = DatabaseManager.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT display_name, phone, location, rate_per_hour FROM freelancer_details WHERE user_id = ?")) {
            
            pstmt.setInt(1, member.getId());
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                VBox detailsBox = new VBox(8);
                detailsBox.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 12; -fx-background-radius: 8;");
                
                String displayName = rs.getString("display_name");
                String phone = rs.getString("phone");
                String location = rs.getString("location");
                String ratePerHour = rs.getString("rate_per_hour");
                
                if (displayName != null && !displayName.isEmpty()) {
                    Label displayLabel = new Label("Display Name: " + displayName);
                    displayLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
                    detailsBox.getChildren().add(displayLabel);
                }
                
                if (phone != null && !phone.isEmpty()) {
                    Label phoneLabel = new Label("📱 " + phone);
                    phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                    detailsBox.getChildren().add(phoneLabel);
                }
                
                if (location != null && !location.isEmpty()) {
                    Label locationLabel = new Label("📍 " + location);
                    locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                    detailsBox.getChildren().add(locationLabel);
                }
                
                if (ratePerHour != null && !ratePerHour.isEmpty()) {
                    Label rateLabel = new Label("💰 Rate: " + ratePerHour + "/hr");
                    rateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
                    detailsBox.getChildren().add(rateLabel);
                }
                
                if (!detailsBox.getChildren().isEmpty()) {
                    card.getChildren().add(detailsBox);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading member details: " + e.getMessage());
        }
        
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
    
    private VBox createProjectCard(Project project) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-cursor: hand;");
        
        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(project.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        Label statusLabel = new Label(project.getStatus());
        String statusColor = "Active".equals(project.getStatus()) ? "#27AE60" : "#95A5A6";
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: " + statusColor + "; -fx-padding: 5 15; -fx-background-radius: 10;");
        
        header.getChildren().addAll(nameLabel, statusLabel);
        
        Label descLabel = new Label(project.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(header, descLabel);
        
        // Add dates if available
        if (project.getStartDate() != null && !project.getStartDate().isEmpty()) {
            Label datesLabel = new Label("Start: " + project.getStartDate() + 
                (project.getEndDate() != null && !project.getEndDate().isEmpty() ? " | End: " + project.getEndDate() : ""));
            datesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
            card.getChildren().add(datesLabel);
        }
        
        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Button toggleStatusBtn = new Button("Active".equals(project.getStatus()) ? "Mark Complete" : "Reactivate");
        toggleStatusBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 15; -fx-background-radius: 15; -fx-cursor: hand;");
        toggleStatusBtn.setOnAction(e -> toggleProjectStatus(project));
        
        if ("Admin".equals(userRole)) {
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 15; -fx-background-radius: 15; -fx-cursor: hand;");
            deleteBtn.setOnAction(e -> deleteProject(project.getId()));
            buttonBox.getChildren().addAll(toggleStatusBtn, deleteBtn);
        } else {
            buttonBox.getChildren().add(toggleStatusBtn);
        }
        
        card.getChildren().add(buttonBox);
        
        return card;
    }
    
    private VBox createRecruitmentCard(RecruitmentPost post) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        
        Label statusLabel = new Label(post.getStatus());
        String statusColor = "Open".equals(post.getStatus()) ? "#27AE60" : "#95A5A6";
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: " + statusColor + "; -fx-padding: 5 15; -fx-background-radius: 10;");
        
        header.getChildren().addAll(titleLabel, statusLabel);
        
        Label descLabel = new Label(post.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(header, descLabel);
        
        // Add skills if available
        if (post.getRequiredSkills() != null && !post.getRequiredSkills().isEmpty()) {
            Label skillsLabel = new Label("Skills: " + post.getRequiredSkills());
            skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151; -fx-font-weight: bold;");
            card.getChildren().add(skillsLabel);
        }
        
        Label positionsLabel = new Label("Positions: " + post.getPositions());
        positionsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        card.getChildren().add(positionsLabel);
        
        // Action buttons (only for admin)
        if ("Admin".equals(userRole)) {
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            
            Button toggleStatusBtn = new Button("Open".equals(post.getStatus()) ? "Close Post" : "Reopen Post");
            toggleStatusBtn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 15; -fx-background-radius: 15; -fx-cursor: hand;");
            toggleStatusBtn.setOnAction(e -> toggleRecruitmentStatus(post));
            
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 15; -fx-background-radius: 15; -fx-cursor: hand;");
            deleteBtn.setOnAction(e -> deleteRecruitmentPost(post.getId()));
            
            buttonBox.getChildren().addAll(toggleStatusBtn, deleteBtn);
            card.getChildren().add(buttonBox);
        }
        
        return card;
    }
    
    private void toggleProjectStatus(Project project) {
        String newStatus = "Active".equals(project.getStatus()) ? "Completed" : "Active";
        boolean updated = ProjectDAO.updateProjectStatus(project.getId(), newStatus);
        
        if (updated) {
            showAlert("Success", "Project status updated!", Alert.AlertType.INFORMATION);
            loadProjects();
            updateDashboardCounts();
        } else {
            showAlert("Error", "Failed to update project status.", Alert.AlertType.ERROR);
        }
    }
    
    private void deleteProject(int projectId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Project");
        confirm.setHeaderText("Are you sure you want to delete this project?");
        confirm.setContentText("This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = ProjectDAO.deleteProject(projectId);
                
                if (deleted) {
                    showAlert("Success", "Project deleted successfully!", Alert.AlertType.INFORMATION);
                    loadProjects();
                    updateDashboardCounts();
                } else {
                    showAlert("Error", "Failed to delete project.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void toggleRecruitmentStatus(RecruitmentPost post) {
        String newStatus = "Open".equals(post.getStatus()) ? "Closed" : "Open";
        boolean updated = RecruitmentPostDAO.updateRecruitmentStatus(post.getId(), newStatus);
        
        if (updated) {
            showAlert("Success", "Recruitment post status updated!", Alert.AlertType.INFORMATION);
            loadRecruitmentPosts();
        } else {
            showAlert("Error", "Failed to update recruitment status.", Alert.AlertType.ERROR);
        }
    }
    
    private void deleteRecruitmentPost(int postId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Recruitment Post");
        confirm.setHeaderText("Are you sure you want to delete this post?");
        confirm.setContentText("This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean deleted = RecruitmentPostDAO.deleteRecruitmentPost(postId);
                
                if (deleted) {
                    showAlert("Success", "Recruitment post deleted successfully!", Alert.AlertType.INFORMATION);
                    loadRecruitmentPosts();
                } else {
                    showAlert("Error", "Failed to delete recruitment post.", Alert.AlertType.ERROR);
                }
            }
        });
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
                    updateDashboardCounts();
                } else {
                    showAlert("Error", "Failed to remove member.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void loadProjects() {
        projectsList.getChildren().clear();
        
        if (teamId == null) {
            return;
        }
        
        int teamIdInt = Integer.parseInt(teamId);
        List<Project> projects = ProjectDAO.getTeamProjects(teamIdInt);
        
        if (projects.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Projects Yet",
                "Create your first project to organize your team's work!",
                "Create Project",
                this::onCreateProject
            );
            projectsList.getChildren().add(emptyState);
        } else {
            for (Project project : projects) {
                VBox projectCard = createProjectCard(project);
                projectsList.getChildren().add(projectCard);
            }
        }
    }
    
    private void loadRecruitmentPosts() {
        recruitmentList.getChildren().clear();
        
        if (teamId == null) {
            return;
        }
        
        int teamIdInt = Integer.parseInt(teamId);
        List<RecruitmentPost> posts = RecruitmentPostDAO.getTeamRecruitmentPosts(teamIdInt);
        
        if (posts.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Recruitment Posts",
                "Post a recruitment to find new team members!",
                "Post Recruitment",
                this::onCreateRecruitmentPost
            );
            recruitmentList.getChildren().add(emptyState);
        } else {
            for (RecruitmentPost post : posts) {
                VBox postCard = createRecruitmentCard(post);
                recruitmentList.getChildren().add(postCard);
            }
        }
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
            actionButton.setStyle("-fx-background-color: #1F2937; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 20; -fx-cursor: hand;");
            actionButton.setOnAction(e -> action.run());
            container.getChildren().add(actionButton);
        }
        
        return container;
    }
    
    private void updateNavigationButtons(Button activeButton) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String activeStyle = "-fx-background-color: #1F2937; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        
        overviewBtn.setStyle(inactiveStyle);
        membersBtn.setStyle(inactiveStyle);
        projectsBtn.setStyle(inactiveStyle);
        recruitmentBtn.setStyle(inactiveStyle);
        jobsBtn.setStyle(inactiveStyle);
        servicesBtn.setStyle(inactiveStyle);
        settingsBtn.setStyle(inactiveStyle);
        
        activeButton.setStyle(activeStyle);
    }
    
    private void hideAllViews() {
        overviewView.setVisible(false);
        membersView.setVisible(false);
        projectsView.setVisible(false);
        recruitmentView.setVisible(false);
        jobsView.setVisible(false);
        servicesView.setVisible(false);
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
    protected void showJobsView() {
        hideAllViews();
        jobsView.setVisible(true);
        updateNavigationButtons(jobsBtn);
        loadAvailableJobs();
    }
    
    @FXML
    protected void showServicesView() {
        hideAllViews();
        servicesView.setVisible(true);
        updateNavigationButtons(servicesBtn);
        loadServices();
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
            
            // Check if there's already a pending invitation
            if (TeamInvitationDAO.hasPendingInvitation(teamIdInt, invitedUser.getId())) {
                showAlert("Error", "This user already has a pending invitation!", Alert.AlertType.ERROR);
                return;
            }
            
            // Create invitation
            User currentUser = SessionManager.getCurrentUser();
            int invitationId = TeamInvitationDAO.createInvitation(teamIdInt, invitedUser.getId(), currentUser.getId());
            
            if (invitationId > 0) {
                // Create notification for invited user
                Team team = TeamDAO.getTeamById(teamIdInt);
                String notificationMessage = "You have been invited to join the team: " + team.getName() + ". Check your invitations to accept or reject.";
                NotificationDAO.createNotification(invitedUser.getId(), "TEAM_INVITE", notificationMessage, String.valueOf(invitationId));
                
                showAlert("Success", "Invitation sent to " + invitedUser.getFirstName() + "!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to send invitation. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }
    
    @FXML
    protected void onCreateProject() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Project");
        dialog.setHeaderText("Add a new project to your team");
        
        // Create form fields
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Project Name");
        nameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Project Description");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setStyle("-fx-font-size: 14px;");
        
        TextField startDateField = new TextField();
        startDateField.setPromptText("Start Date (YYYY-MM-DD)");
        startDateField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        TextField endDateField = new TextField();
        endDateField.setPromptText("End Date (YYYY-MM-DD)");
        endDateField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        content.getChildren().addAll(
            new Label("Project Name:"),
            nameField,
            new Label("Description:"),
            descriptionArea,
            new Label("Start Date:"),
            startDateField,
            new Label("End Date:"),
            endDateField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();
                
                if (name.isEmpty() || description.isEmpty()) {
                    showAlert("Error", "Please fill in project name and description!", Alert.AlertType.ERROR);
                    return;
                }
                
                User currentUser = SessionManager.getCurrentUser();
                int teamIdInt = Integer.parseInt(teamId);
                
                int projectId = ProjectDAO.createProject(teamIdInt, name, description, startDate, endDate, currentUser.getId());
                
                if (projectId > 0) {
                    showAlert("Success", "Project created successfully!", Alert.AlertType.INFORMATION);
                    loadProjects();
                    updateDashboardCounts();
                } else {
                    showAlert("Error", "Failed to create project. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    @FXML
    protected void onCreateRecruitmentPost() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Recruitment Post");
        dialog.setHeaderText("Post a recruitment to find new team members");
        
        // Create form fields
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        TextField titleField = new TextField();
        titleField.setPromptText("Position Title");
        titleField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Job Description");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setStyle("-fx-font-size: 14px;");
        
        TextField skillsField = new TextField();
        skillsField.setPromptText("Required Skills (comma separated)");
        skillsField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        TextField positionsField = new TextField();
        positionsField.setPromptText("Number of Positions");
        positionsField.setText("1");
        positionsField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        
        content.getChildren().addAll(
            new Label("Position Title:"),
            titleField,
            new Label("Description:"),
            descriptionArea,
            new Label("Required Skills:"),
            skillsField,
            new Label("Number of Positions:"),
            positionsField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                String skills = skillsField.getText().trim();
                String positionsText = positionsField.getText().trim();
                
                if (title.isEmpty() || description.isEmpty()) {
                    showAlert("Error", "Please fill in title and description!", Alert.AlertType.ERROR);
                    return;
                }
                
                int positions = 1;
                try {
                    positions = Integer.parseInt(positionsText);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Number of positions must be a valid number!", Alert.AlertType.ERROR);
                    return;
                }
                
                User currentUser = SessionManager.getCurrentUser();
                int teamIdInt = Integer.parseInt(teamId);
                
                int postId = RecruitmentPostDAO.createRecruitmentPost(teamIdInt, title, description, skills, positions, currentUser.getId());
                
                if (postId > 0) {
                    showAlert("Success", "Recruitment post created successfully!", Alert.AlertType.INFORMATION);
                    loadRecruitmentPosts();
                } else {
                    showAlert("Error", "Failed to create recruitment post. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
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
    
    private void loadAvailableJobs() {
        jobsList.getChildren().clear();
        
        List<Job> jobs = JobDAO.getAllOpenJobs();
        
        if (jobs.isEmpty()) {
            Label placeholder = new Label("No jobs available at the moment. Check back later!");
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            jobsList.getChildren().add(placeholder);
        } else {
            for (Job job : jobs) {
                VBox jobCard = createJobCard(job);
                jobsList.getChildren().add(jobCard);
            }
        }
    }
    
    private VBox createJobCard(Job job) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                     "-fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        // Header with title and client name
        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        javafx.scene.text.Text titleText = new javafx.scene.text.Text(job.getTitle());
        titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1F2937;");
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label statusLabel = new Label("Open");
        statusLabel.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                           "-fx-padding: 5 12; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(titleText, spacer, statusLabel);
        
        // Client info
        String clientName = JobDAO.getClientName(job.getClientId());
        Label clientLabel = new Label("Posted by: " + clientName);
        clientLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-style: italic;");
        
        // Description
        Label descLabel = new Label(job.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444; -fx-padding: 8 0;");
        
        // Details section with budget and duration
        HBox details = new HBox(25);
        details.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        details.setStyle("-fx-padding: 10 0;");
        
        // Budget
        VBox budgetBox = new VBox(3);
        Label budgetTitleLabel = new Label("Budget");
        budgetTitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: 600;");
        Label budgetValue = new Label(job.getBudget());
        budgetValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
        budgetBox.getChildren().addAll(budgetTitleLabel, budgetValue);
        
        // Duration
        VBox durationBox = new VBox(3);
        Label durationTitleLabel = new Label("Duration");
        durationTitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: 600;");
        Label durationValue = new Label(job.getDuration() != null && !job.getDuration().isEmpty() ? 
                                        job.getDuration() : "Not specified");
        durationValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        durationBox.getChildren().addAll(durationTitleLabel, durationValue);
        
        details.getChildren().addAll(budgetBox, durationBox);
        
        // Skills section
        VBox skillsSection = new VBox(8);
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            Label skillsTitle = new Label("Required Skills:");
            skillsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #333;");
            
            Label skillsLabel = new Label(job.getSkills());
            skillsLabel.setWrapText(true);
            skillsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1F2937; " +
                               "-fx-background-color: #F3F4F6; -fx-padding: 10; -fx-background-radius: 8;");
            
            skillsSection.getChildren().addAll(skillsTitle, skillsLabel);
        }
        
        // Posted date
        if (job.getPostedDate() != null) {
            Label dateLabel = new Label("Posted: " + job.getPostedDate());
            dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
            card.getChildren().addAll(header, clientLabel, descLabel, details, skillsSection, dateLabel);
        } else {
            card.getChildren().addAll(header, clientLabel, descLabel, details, skillsSection);
        }
        
        // Apply button
        // Check if already applied
        boolean hasApplied = JobApplicationDAO.hasApplied(job.getId(), Integer.parseInt(teamId));
        
        if (hasApplied) {
            // Get application status
            List<JobApplication> apps = JobApplicationDAO.getApplicationsByJobId(job.getId());
            JobApplication teamApp = null;
            for (JobApplication app : apps) {
                if (app.getTeamId() == Integer.parseInt(teamId)) {
                    teamApp = app;
                    break;
                }
            }
            
            HBox statusBox = new HBox(10);
            statusBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            statusBox.setStyle("-fx-padding: 10 0 0 0;");
            
            Label statusIcon = new Label(switch (teamApp != null ? teamApp.getStatus() : "Unknown") {
                case "Pending" -> "⏳";
                case "Approved" -> "✅";
                case "Rejected" -> "❌";
                default -> "❓";
            });
            statusIcon.setStyle("-fx-font-size: 20px;");
            
            Label statusText = new Label(teamApp != null ? 
                "Application Status: " + teamApp.getStatus() : "Application Submitted");
            statusText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " +
                (teamApp != null && "Approved".equals(teamApp.getStatus()) ? "#10B981" :
                 teamApp != null && "Rejected".equals(teamApp.getStatus()) ? "#EF4444" : "#F59E0B") + ";");
            
            statusBox.getChildren().addAll(statusIcon, statusText);
            card.getChildren().add(statusBox);
            
            if (teamApp != null && "Approved".equals(teamApp.getStatus())) {
                Label projectInfo = new Label("✓ Project has been created for your team!");
                projectInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #10B981; -fx-font-style: italic;");
                card.getChildren().add(projectInfo);
            }
        } else {
            Button applyBtn = new Button("Express Interest");
            applyBtn.setStyle("-fx-background-color: #1F2937; -fx-text-fill: white; " +
                             "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 30; " +
                             "-fx-background-radius: 8; -fx-cursor: hand;");
            applyBtn.setOnAction(e -> expressInterest(job));
            
            HBox buttonBox = new HBox(applyBtn);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            buttonBox.setStyle("-fx-padding: 10 0 0 0;");
            card.getChildren().add(buttonBox);
        }
        
        return card;
    }
    
    private void expressInterest(Job job) {
        // Check if already applied
        if (JobApplicationDAO.hasApplied(job.getId(), Integer.parseInt(teamId))) {
            showAlert("Already Applied", "Your team has already applied for this job.", Alert.AlertType.INFORMATION);
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Express Interest");
        confirmDialog.setHeaderText("Apply for this job?");
        confirmDialog.setContentText("Job: " + job.getTitle() + "\n\n" +
                                    "This will create a formal application and notify the client.");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Create job application
                int applicationId = JobApplicationDAO.createJobApplication(job.getId(), Integer.parseInt(teamId));
                
                if (applicationId > 0) {
                    // Create notification for the client
                    String message = "Team \"" + teamNameLabel.getText() + "\" has applied for your job: " + job.getTitle();
                    NotificationDAO.createNotification(job.getClientId(), "job_application", message, String.valueOf(applicationId));
                    
                    showAlert("Success", "Your application has been submitted! The client will review and respond soon.", 
                             Alert.AlertType.INFORMATION);
                    loadAvailableJobs(); // Refresh the list to show application status
                } else {
                    showAlert("Error", "Failed to submit application. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    @FXML
    protected void onCreateService() {
        if (teamId == null) {
            showAlert("Error", "No team selected!", Alert.AlertType.ERROR);
            return;
        }
        
        // Create dialog for service details
        Dialog<ServicePost> dialog = new Dialog<>();
        dialog.setTitle("Create Service Post");
        dialog.setHeaderText("Post a new service that your team offers");
        
        // Set button types
        ButtonType createButtonType = new ButtonType("Create Service", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create form fields
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        TextField titleField = new TextField();
        titleField.setPromptText("e.g., Web Development, Mobile App Design");
        titleField.setStyle("-fx-font-size: 13px; -fx-padding: 10;");
        
        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe what your team offers in detail...");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);
        descArea.setStyle("-fx-font-size: 13px;");
        
        TextField categoryField = new TextField();
        categoryField.setPromptText("e.g., Development, Design, Marketing");
        categoryField.setStyle("-fx-font-size: 13px; -fx-padding: 10;");
        
        TextField pricingField = new TextField();
        pricingField.setPromptText("e.g., $500-$2000, Hourly: $50");
        pricingField.setStyle("-fx-font-size: 13px; -fx-padding: 10;");
        
        TextField deliveryField = new TextField();
        deliveryField.setPromptText("e.g., 1-2 weeks, 3-5 days");
        deliveryField.setStyle("-fx-font-size: 13px; -fx-padding: 10;");
        
        content.getChildren().addAll(
            new Label("Service Title *"), titleField,
            new Label("Description *"), descArea,
            new Label("Category"), categoryField,
            new Label("Pricing"), pricingField,
            new Label("Delivery Time"), deliveryField
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (titleField.getText().trim().isEmpty() || descArea.getText().trim().isEmpty()) {
                    showAlert("Error", "Please fill in required fields (Title and Description)", Alert.AlertType.ERROR);
                    return null;
                }
                
                User currentUser = SessionManager.getCurrentUser();
                ServicePost service = new ServicePost(
                    Integer.parseInt(teamId),
                    titleField.getText().trim(),
                    descArea.getText().trim(),
                    categoryField.getText().trim(),
                    pricingField.getText().trim(),
                    deliveryField.getText().trim(),
                    currentUser.getId()
                );
                return service;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(service -> {
            if (ServicePostDAO.createServicePost(service)) {
                showAlert("Success", "Service posted successfully! Clients can now see and request this service.", 
                         Alert.AlertType.INFORMATION);
                loadServices();
            } else {
                showAlert("Error", "Failed to create service post. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }
    
    private void loadServices() {
        servicesList.getChildren().clear();
        
        if (teamId == null) {
            return;
        }
        
        List<ServicePost> services = ServicePostDAO.getTeamServicePosts(Integer.parseInt(teamId));
        
        if (services.isEmpty()) {
            Label placeholder = new Label("No services posted yet. Click '+ Post Service' to create one!");
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            servicesList.getChildren().add(placeholder);
        } else {
            for (ServicePost service : services) {
                VBox serviceCard = createServiceCard(service);
                servicesList.getChildren().add(serviceCard);
            }
        }
    }
    
    private VBox createServiceCard(ServicePost service) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                     "-fx-border-color: #E0E0E0; -fx-border-radius: 12; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        // Header with title and status
        HBox header = new HBox(15);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        javafx.scene.text.Text titleText = new javafx.scene.text.Text(service.getTitle());
        titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1F2937;");
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label statusLabel = new Label(service.getStatus());
        String statusColor = service.getStatus().equals("Active") ? "#10B981" : "#6B7280";
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; " +
                           "-fx-padding: 5 12; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(titleText, spacer, statusLabel);
        
        // Description
        Label descLabel = new Label(service.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        
        card.getChildren().addAll(header, descLabel);
        
        // Details grid
        if ((service.getCategory() != null && !service.getCategory().isEmpty()) ||
            (service.getPricing() != null && !service.getPricing().isEmpty()) ||
            (service.getDeliveryTime() != null && !service.getDeliveryTime().isEmpty())) {
            
            HBox details = new HBox(20);
            details.setStyle("-fx-padding: 10 0;");
            
            if (service.getCategory() != null && !service.getCategory().isEmpty()) {
                VBox categoryBox = new VBox(3);
                Label catLabel = new Label("Category");
                catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                Label catValue = new Label(service.getCategory());
                catValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #333;");
                categoryBox.getChildren().addAll(catLabel, catValue);
                details.getChildren().add(categoryBox);
            }
            
            if (service.getPricing() != null && !service.getPricing().isEmpty()) {
                VBox priceBox = new VBox(3);
                Label priceLabel = new Label("Pricing");
                priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                Label priceValue = new Label(service.getPricing());
                priceValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #10B981;");
                priceBox.getChildren().addAll(priceLabel, priceValue);
                details.getChildren().add(priceBox);
            }
            
            if (service.getDeliveryTime() != null && !service.getDeliveryTime().isEmpty()) {
                VBox deliveryBox = new VBox(3);
                Label deliveryLabel = new Label("Delivery");
                deliveryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                Label deliveryValue = new Label(service.getDeliveryTime());
                deliveryValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #333;");
                deliveryBox.getChildren().addAll(deliveryLabel, deliveryValue);
                details.getChildren().add(deliveryBox);
            }
            
            card.getChildren().add(details);
        }
        
        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        actions.setStyle("-fx-padding: 10 0 0 0;");
        
        Button viewRequestsBtn = new Button("View Requests");
        viewRequestsBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                                "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
        viewRequestsBtn.setOnAction(e -> showServiceRequestsDialog(service));
        
        Button toggleStatusBtn = new Button(service.getStatus().equals("Active") ? "Deactivate" : "Activate");
        toggleStatusBtn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; " +
                                "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        toggleStatusBtn.setOnAction(e -> {
            String newStatus = service.getStatus().equals("Active") ? "Inactive" : "Active";
            if (ServicePostDAO.updateServiceStatus(service.getId(), newStatus)) {
                showAlert("Success", "Service status updated!", Alert.AlertType.INFORMATION);
                loadServices();
            }
        });
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; " +
                          "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Service");
            confirm.setHeaderText("Are you sure?");
            confirm.setContentText("Delete service: " + service.getTitle());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK && ServicePostDAO.deleteServicePost(service.getId())) {
                    showAlert("Success", "Service deleted!", Alert.AlertType.INFORMATION);
                    loadServices();
                }
            });
        });
        
        actions.getChildren().addAll(viewRequestsBtn, toggleStatusBtn, deleteBtn);
        card.getChildren().add(actions);
        
        return card;
    }
    
    private void showServiceRequestsDialog(ServicePost service) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Service Requests");
        dialog.setHeaderText("Requests for: " + service.getTitle());
        
        VBox content = new VBox(15);
        content.setPrefWidth(550);
        content.setMaxHeight(400);
        
        List<ServiceRequest> requests = ServiceRequestDAO.getRequestsByServiceId(service.getId());
        
        if (requests.isEmpty()) {
            Label noRequests = new Label("No collaboration requests yet.");
            noRequests.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            content.getChildren().add(noRequests);
        } else {
            ScrollPane scroll = new ScrollPane();
            VBox requestsList = new VBox(12);
            requestsList.setStyle("-fx-padding: 10;");
            
            for (ServiceRequest request : requests) {
                VBox requestCard = new VBox(10);
                requestCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #E0E0E0; " +
                                   "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1;");
                
                // Get client details
                User client = UserDAO.getUserById(request.getClientId());
                if (client == null) continue;
                
                Label clientName = new Label("Client: " + client.getFirstName() + " " + client.getLastName());
                clientName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");
                
                Label clientEmail = new Label("\ud83d\udce7 " + client.getEmail());
                clientEmail.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
                
                Label requestDate = new Label("Requested: " + request.getRequestDate());
                requestDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
                
                Label statusLabel = new Label("Status: " + request.getStatus());
                String statusColor = request.getStatus().equals("Pending") ? "#F59E0B" : 
                                    request.getStatus().equals("Approved") ? "#10B981" : "#EF4444";
                statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; " +
                                   "-fx-padding: 4 10; -fx-background-radius: 10; -fx-font-size: 12px; -fx-font-weight: bold;");
                
                requestCard.getChildren().addAll(clientName, clientEmail, requestDate, statusLabel);
                
                // Add approve/reject buttons for pending requests
                if (request.getStatus().equals("Pending")) {
                    HBox buttonsBox = new HBox(10);
                    
                    Button approveBtn = new Button("\u2713 Approve");
                    approveBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                                      "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
                    approveBtn.setOnAction(e -> {
                        boolean success = ServiceRequestDAO.approveRequest(
                            request.getId(), service.getId(), request.getClientId());
                        
                        if (success) {
                            showAlert("Success", "Request approved! Client has been notified.", 
                                     Alert.AlertType.INFORMATION);
                            dialog.close();
                        } else {
                            showAlert("Error", "Failed to approve request.", Alert.AlertType.ERROR);
                        }
                    });
                    
                    Button rejectBtn = new Button("\u2717 Reject");
                    rejectBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; " +
                                      "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
                    rejectBtn.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Reject Request");
                        confirm.setHeaderText("Reject collaboration request?");
                        confirm.setContentText("Client: " + client.getFirstName() + " " + client.getLastName());
                        
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                boolean success = ServiceRequestDAO.rejectRequest(
                                    request.getId(), request.getClientId());
                                
                                if (success) {
                                    showAlert("Info", "Request rejected. Client has been notified.", 
                                             Alert.AlertType.INFORMATION);
                                    dialog.close();
                                } else {
                                    showAlert("Error", "Failed to reject request.", Alert.AlertType.ERROR);
                                }
                            }
                        });
                    });
                    
                    buttonsBox.getChildren().addAll(approveBtn, rejectBtn);
                    requestCard.getChildren().add(buttonsBox);
                }
                
                requestsList.getChildren().add(requestCard);
            }
            
            scroll.setContent(requestsList);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            content.getChildren().add(scroll);
        }
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
