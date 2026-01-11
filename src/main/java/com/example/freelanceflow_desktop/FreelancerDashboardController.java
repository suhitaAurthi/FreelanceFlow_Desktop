package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FreelancerDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImageView;
    @FXML private Label teamsCountLabel;
    @FXML private Label jobsCountLabel;
    @FXML private Label recruitmentsCountLabel;
    @FXML private Label notificationBadge;
    @FXML private Button myTeamsBtn;
    @FXML private Button createTeamBtn;
    @FXML private Button browseTeamsBtn;
    @FXML private Button recruitmentsBtn;
    @FXML private Button jobsBtn;
    @FXML private Button projectsBtn;
    @FXML private Button notificationsBtn;
    @FXML private StackPane contentArea;
    @FXML private VBox myTeamsView;
    @FXML private VBox createTeamView;
    @FXML private VBox browseTeamsView;
    @FXML private VBox recruitmentsView;
    @FXML private VBox jobsView;
    @FXML private VBox projectsView;
    
    // Create Team fields
    @FXML private TextField teamNameField;
    @FXML private TextArea teamDescriptionArea;
    @FXML private TextField teamSkillsField;
    @FXML private TextField teamMaxMembersField;
    @FXML private TextField searchTeamsField;
    
    // Lists
    @FXML private VBox myTeamsList;
    @FXML private VBox browseTeamsList;
    @FXML private VBox recruitmentsList;
    @FXML private VBox jobsList;
    @FXML private VBox projectsList;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + "!");
            
            // Try to load profile photo but don't let it block dashboard loading
            try {
                loadProfilePhoto(currentUser.getId());
            } catch (Exception e) {
                System.err.println("Non-critical: Failed to load profile photo - " + e.getMessage());
            }
        }
        
        // Add search listener
        if (searchTeamsField != null) {
            searchTeamsField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTeams(newValue);
            });
        }
        
        // Update navigation button styles
        updateNavigationButtons(myTeamsBtn);
        
        // Load initial data and update counts
        loadMyTeams();
        updateDashboardCounts();
        loadNotificationCount();
    }
    
    private void loadProfilePhoto(int userId) {
        if (profileImageView == null) {
            System.err.println("ProfileImageView is not initialized");
            return;
        }
        
        String photoPath = FreelancerDetailsDAO.getProfilePhotoPath(userId);
        
        if (photoPath != null && !photoPath.isEmpty()) {
            File photoFile = new File(photoPath);
            if (photoFile.exists()) {
                try (FileInputStream fis = new FileInputStream(photoFile)) {
                    Image image = new Image(fis);
                    profileImageView.setImage(image);
                    
                    // Make it circular
                    Circle clip = new Circle(22.5, 22.5, 22.5);
                    profileImageView.setClip(clip);
                } catch (Exception e) {
                    System.err.println("Error loading image file: " + e.getMessage());
                }
            }
        }
    }
    
    private void loadNotificationCount() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            int unreadCount = NotificationDAO.getUnreadCount(currentUser.getId());
            int pendingInvitations = TeamInvitationDAO.getUserPendingInvitations(currentUser.getId()).size();
            int totalCount = unreadCount + pendingInvitations;
            
            if (totalCount > 0) {
                notificationBadge.setText(String.valueOf(totalCount));
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }
    
    @FXML
    protected void showNotifications() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;
        
        // Check for pending team invitations first
        List<TeamInvitation> pendingInvitations = TeamInvitationDAO.getUserPendingInvitations(currentUser.getId());
        
        if (!pendingInvitations.isEmpty()) {
            showTeamInvitations(pendingInvitations);
            return;
        }
        
        // Show regular notifications
        List<Notification> notifications = NotificationDAO.getUserNotifications(currentUser.getId());
        
        if (notifications.isEmpty()) {
            showAlert("Notifications", "You have no notifications.", Alert.AlertType.INFORMATION);
            return;
        }
        
        StringBuilder message = new StringBuilder();
        for (Notification notif : notifications) {
            String readStatus = notif.isRead() ? "✓" : "●";
            message.append(readStatus).append(" ").append(notif.getMessage()).append("\n\n");
            
            // Mark as read
            if (!notif.isRead()) {
                NotificationDAO.markAsRead(notif.getId());
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Your Notifications");
        alert.setContentText(message.toString());
        alert.showAndWait();
        
        loadNotificationCount();
    }
    
    private void showTeamInvitations(List<TeamInvitation> invitations) {
        if (invitations.isEmpty()) {
            return;
        }
        
        // Show first invitation
        TeamInvitation invitation = invitations.get(0);
        Team team = TeamDAO.getTeamById(invitation.getTeamId());
        User invitedBy = UserDAO.getUserById(invitation.getInvitedBy());
        
        if (team == null) {
            return;
        }
        
        String inviterName = invitedBy != null ? invitedBy.getFirstName() + " " + invitedBy.getLastName() : "Someone";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Team Invitation");
        alert.setHeaderText("You have been invited to join a team!");
        alert.setContentText("Team: " + team.getName() + "\n" +
                           "Invited by: " + inviterName + "\n\n" +
                           "Do you want to accept this invitation?");
        
        ButtonType acceptBtn = new ButtonType("Accept");
        ButtonType rejectBtn = new ButtonType("Reject");
        ButtonType laterBtn = new ButtonType("Later", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(acceptBtn, rejectBtn, laterBtn);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == acceptBtn) {
                // Accept invitation
                boolean accepted = TeamInvitationDAO.acceptInvitation(invitation.getId());
                
                if (accepted) {
                    // Add user to team
                    boolean added = TeamDAO.addTeamMember(team.getId(), invitation.getUserId(), "Member");
                    
                    if (added) {
                        showAlert("Success", "You have joined " + team.getName() + "!", Alert.AlertType.INFORMATION);
                        loadMyTeams();
                        loadNotificationCount();
                        
                        // Check if there are more invitations
                        if (invitations.size() > 1) {
                            showTeamInvitations(invitations.subList(1, invitations.size()));
                        }
                    } else {
                        showAlert("Error", "Failed to join team. Please try again.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Error", "Failed to process invitation.", Alert.AlertType.ERROR);
                }
            } else if (response == rejectBtn) {
                // Reject invitation
                boolean rejected = TeamInvitationDAO.rejectInvitation(invitation.getId());
                
                if (rejected) {
                    showAlert("Info", "Invitation rejected.", Alert.AlertType.INFORMATION);
                    loadNotificationCount();
                    
                    // Check if there are more invitations
                    if (invitations.size() > 1) {
                        showTeamInvitations(invitations.subList(1, invitations.size()));
                    }
                } else {
                    showAlert("Error", "Failed to reject invitation.", Alert.AlertType.ERROR);
                }
            }
            // If "Later" is clicked, do nothing
        });
    }
    
    private void updateDashboardCounts() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            teamsCountLabel.setText("0");
            jobsCountLabel.setText("0");
            recruitmentsCountLabel.setText("0");
            return;
        }
        
        // Get actual counts from database
        int teamCount = TeamDAO.getUserTeams(currentUser.getId()).size();
        int recruitmentCount = RecruitmentPostDAO.getAllOpenRecruitmentPosts().size();
        
        teamsCountLabel.setText(String.valueOf(teamCount));
        jobsCountLabel.setText("0"); // Jobs feature not implemented yet
        recruitmentsCountLabel.setText(String.valueOf(recruitmentCount));
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
        if (projectsBtn != null) projectsBtn.setStyle(inactiveStyle);
        
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

    @FXML
    protected void showProjectsView() {
        hideAllViews();
        projectsView.setVisible(true);
        updateNavigationButtons(projectsBtn);
        loadProjects();
    }

    private void hideAllViews() {
        myTeamsView.setVisible(false);
        createTeamView.setVisible(false);
        browseTeamsView.setVisible(false);
        recruitmentsView.setVisible(false);
        jobsView.setVisible(false);
        if (projectsView != null) projectsView.setVisible(false);
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

        // Parse max members (default to 10 if not specified or invalid)
        int maxMembersInt = 10;
        if (!maxMembers.isEmpty()) {
            try {
                maxMembersInt = Integer.parseInt(maxMembers);
            } catch (NumberFormatException e) {
                showAlert("Error", "Maximum members must be a number!", Alert.AlertType.ERROR);
                return;
            }
        }

        // Save team to database
        int teamId = TeamDAO.createTeam(name, description, skills, maxMembersInt, currentUser.getId());
        
        if (teamId > 0) {
            // Get the created team
            Team createdTeam = TeamDAO.getTeamById(teamId);
            
            if (createdTeam != null) {
                // Redirect to team dashboard as admin
                navigateToTeamDashboard(createdTeam, "Admin");
            } else {
                showAlert("Success", "Team created successfully!", Alert.AlertType.INFORMATION);
                loadMyTeams();
                showMyTeamsView();
            }
        } else {
            showAlert("Error", "Failed to create team. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void loadMyTeams() {
        myTeamsList.getChildren().clear();
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Load user's teams from database
        List<Team> teams = TeamDAO.getUserTeams(currentUser.getId());
        
        if (teams.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Teams Yet",
                "You haven't joined any teams yet. Create a new team or browse existing ones!",
                "Create Team",
                this::showCreateTeamView
            );
            myTeamsList.getChildren().add(emptyState);
        } else {
            for (Team team : teams) {
                VBox teamCard = createTeamCard(team);
                myTeamsList.getChildren().add(teamCard);
            }
        }
        
        // Update dashboard counts
        updateDashboardCounts();
    }
    
    private VBox createTeamCard(Team team) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-cursor: hand;");
        
        User currentUser = SessionManager.getCurrentUser();
        String role = TeamDAO.getUserTeamRole(team.getId(), currentUser.getId());
        
        Label nameLabel = new Label(team.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E6470;");
        
        Label descLabel = new Label(team.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        Label roleLabel = new Label("Role: " + role);
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #2B8994; -fx-padding: 5 15; -fx-background-radius: 10;");
        
        Button openBtn = new Button("Open Team Dashboard");
        openBtn.setStyle("-fx-background-color: #2B8994; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 20; -fx-cursor: hand;");
        openBtn.setOnAction(e -> navigateToTeamDashboard(team, role));
        
        HBox buttonBox = new HBox(openBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        card.getChildren().addAll(nameLabel, descLabel, roleLabel, buttonBox);
        
        return card;
    }
    
    private void navigateToTeamDashboard(Team team, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamDashboard.fxml"));
            Parent root = loader.load();
            
            TeamDashboardController controller = loader.getController();
            controller.setTeamData(String.valueOf(team.getId()), team.getName(), role);
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load team dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAvailableTeams() {
        browseTeamsList.getChildren().clear();
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Load all teams from database
        List<Team> allTeams = TeamDAO.getAllTeams();
        
        if (allTeams.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Teams Available",
                "There are no teams available to join at the moment. Be the first to create one!",
                null,
                null
            );
            browseTeamsList.getChildren().add(emptyState);
        } else {
            // Get user's teams for membership checking
            List<Team> userTeams = TeamDAO.getUserTeams(currentUser.getId());
            List<Integer> userTeamIds = userTeams.stream().map(Team::getId).toList();
            
            for (Team team : allTeams) {
                boolean isAlreadyMember = userTeamIds.contains(team.getId());
                VBox teamCard = createBrowseTeamCard(team, isAlreadyMember);
                browseTeamsList.getChildren().add(teamCard);
            }
        }
    }
    
    private void filterTeams(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadAvailableTeams();
            return;
        }
        
        browseTeamsList.getChildren().clear();
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;
        
        String searchLower = searchText.toLowerCase().trim();
        List<Team> allTeams = TeamDAO.getAllTeams();
        List<Team> userTeams = TeamDAO.getUserTeams(currentUser.getId());
        List<Integer> userTeamIds = userTeams.stream().map(Team::getId).toList();
        
        boolean foundAny = false;
        for (Team team : allTeams) {
            // Search in team name, description, and skills
            boolean matches = team.getName().toLowerCase().contains(searchLower) ||
                            (team.getDescription() != null && team.getDescription().toLowerCase().contains(searchLower)) ||
                            (team.getSkills() != null && team.getSkills().toLowerCase().contains(searchLower));
            
            if (matches) {
                foundAny = true;
                boolean isAlreadyMember = userTeamIds.contains(team.getId());
                VBox teamCard = createBrowseTeamCard(team, isAlreadyMember);
                browseTeamsList.getChildren().add(teamCard);
            }
        }
        
        if (!foundAny) {
            Label noResults = new Label("No teams found matching '" + searchText + "'");
            noResults.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            browseTeamsList.getChildren().add(noResults);
        }
    }
    
    private VBox createBrowseTeamCard(Team team, boolean isAlreadyMember) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        Label nameLabel = new Label(team.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E6470;");
        
        Label descLabel = new Label(team.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(nameLabel, descLabel);
        
        // Add skills if available
        if (team.getSkills() != null && !team.getSkills().isEmpty()) {
            Label skillsLabel = new Label("Skills: " + team.getSkills());
            skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2B8994; -fx-font-weight: bold;");
            card.getChildren().add(skillsLabel);
        }
        
        // Get team member count
        int memberCount = TeamDAO.getTeamMembers(team.getId()).size();
        int maxMembers = team.getMaxMembers();
        Label membersLabel = new Label("Members: " + memberCount + "/" + maxMembers);
        membersLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        card.getChildren().add(membersLabel);
        
        // Button logic based on membership status
        Button actionBtn = new Button();
        User currentUser = SessionManager.getCurrentUser();
        
        if (isAlreadyMember) {
            // Already a member - show joined status
            actionBtn.setText("✓ Already Joined");
            actionBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 13px; " +
                             "-fx-padding: 8 20; -fx-background-radius: 20; -fx-cursor: default;");
            actionBtn.setDisable(true);
        } else if (memberCount >= maxMembers) {
            // Team is full
            actionBtn.setText("Team Full");
            actionBtn.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 13px; " +
                             "-fx-padding: 8 20; -fx-background-radius: 20;");
            actionBtn.setDisable(true);
        } else if (TeamInvitationDAO.hasPendingInvitation(team.getId(), currentUser.getId())) {
            // Already has pending invitation
            actionBtn.setText("Request Pending");
            actionBtn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 13px; " +
                             "-fx-padding: 8 20; -fx-background-radius: 20;");
            actionBtn.setDisable(true);
        } else {
            // Can request to join
            actionBtn.setText("Request to Join");
            actionBtn.setStyle("-fx-background-color: #2B8994; -fx-text-fill: white; -fx-font-size: 13px; " +
                             "-fx-padding: 8 20; -fx-background-radius: 20; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> requestToJoinTeam(team));
        }
        
        HBox buttonBox = new HBox(actionBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        card.getChildren().add(buttonBox);
        
        return card;
    }
    
    private void requestToJoinTeam(Team team) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Request to Join Team");
        confirm.setHeaderText("Join " + team.getName());
        confirm.setContentText("Do you want to request to join this team?\n\nThe team admin will be notified of your request.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                User currentUser = SessionManager.getCurrentUser();
                
                // Create invitation request (admin can accept/reject later)
                int invitationId = TeamInvitationDAO.createInvitation(team.getId(), currentUser.getId(), team.getAdminId());
                
                if (invitationId > 0) {
                    // Notify team admin
                    String notificationMessage = currentUser.getFirstName() + " " + currentUser.getLastName() + 
                                                " has requested to join your team: " + team.getName();
                    NotificationDAO.createNotification(team.getAdminId(), "TEAM_REQUEST", notificationMessage, String.valueOf(invitationId));
                    
                    showAlert("Success", "Your join request has been sent to the team admin!", Alert.AlertType.INFORMATION);
                    loadAvailableTeams();
                } else {
                    showAlert("Error", "Failed to send request. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void loadRecruitments() {
        recruitmentsList.getChildren().clear();
        
        // Load all open recruitment posts from database
        List<RecruitmentPost> posts = RecruitmentPostDAO.getAllOpenRecruitmentPosts();
        
        if (posts.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Recruitment Posts",
                "There are no recruitment posts available yet. Check back later!",
                null,
                null
            );
            recruitmentsList.getChildren().add(emptyState);
        } else {
            for (RecruitmentPost post : posts) {
                VBox postCard = createRecruitmentCard(post);
                recruitmentsList.getChildren().add(postCard);
            }
        }
    }
    
    private VBox createRecruitmentCard(RecruitmentPost post) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        // Get team info
        Team team = TeamDAO.getTeamById(post.getTeamId());
        String teamName = team != null ? team.getName() : "Unknown Team";
        
        Label teamLabel = new Label(teamName);
        teamLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2B8994; -fx-font-weight: bold;");
        
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E6470;");
        
        Label descLabel = new Label(post.getDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        card.getChildren().addAll(teamLabel, titleLabel, descLabel);
        
        // Add skills if available
        if (post.getRequiredSkills() != null && !post.getRequiredSkills().isEmpty()) {
            Label skillsLabel = new Label("Skills: " + post.getRequiredSkills());
            skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2B8994; -fx-font-weight: bold;");
            card.getChildren().add(skillsLabel);
        }
        
        Label positionsLabel = new Label("Positions: " + post.getPositions());
        positionsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        card.getChildren().add(positionsLabel);
        
        // Apply button
        Button applyBtn = new Button("Apply");
        applyBtn.setStyle("-fx-background-color: #2B8994; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 20; -fx-cursor: hand;");
        applyBtn.setOnAction(e -> applyToRecruitment(post, team));
        
        HBox buttonBox = new HBox(applyBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        card.getChildren().add(buttonBox);
        
        return card;
    }
    
    private void applyToRecruitment(RecruitmentPost post, Team team) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Apply for Position");
        confirm.setHeaderText("Apply to join " + team.getName());
        confirm.setContentText("Do you want to apply for: " + post.getTitle() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                User currentUser = SessionManager.getCurrentUser();
                
                // Check if already in team
                if (TeamDAO.isUserInTeam(team.getId(), currentUser.getId())) {
                    showAlert("Info", "You are already a member of this team!", Alert.AlertType.INFORMATION);
                    return;
                }
                
                // For now, directly add to team (in production, this should create an application)
                boolean added = TeamDAO.addTeamMember(team.getId(), currentUser.getId(), "Member");
                
                if (added) {
                    // Notify team admin
                    String notificationMessage = currentUser.getFirstName() + " " + currentUser.getLastName() + 
                                                " has applied to join your team for: " + post.getTitle();
                    NotificationDAO.createNotification(team.getAdminId(), "TEAM_APPLICATION", notificationMessage, String.valueOf(team.getId()));
                    
                    showAlert("Success", "You have successfully joined the team!", Alert.AlertType.INFORMATION);
                    loadMyTeams();
                } else {
                    showAlert("Error", "Failed to apply. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
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
    
    private void loadProjects() {
        projectsList.getChildren().clear();
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Get all projects from teams the freelancer belongs to
        List<Project> projects = ProjectDAO.getFreelancerProjects(currentUser.getId());
        
        if (projects.isEmpty()) {
            VBox emptyState = createEmptyState(
                "No Projects Yet",
                "Projects will appear here when your teams are assigned work by clients.",
                null,
                null
            );
            projectsList.getChildren().add(emptyState);
            return;
        }
        
        // Display each project
        for (Project project : projects) {
            projectsList.getChildren().add(createProjectCard(project));
        }
    }
    
    private VBox createProjectCard(Project project) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-cursor: hand;");
        
        // Project name
        Label nameLabel = new Label(project.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E6470;");
        nameLabel.setWrapText(true);
        card.getChildren().add(nameLabel);
        
        // Team info
        Team team = TeamDAO.getTeamById(project.getTeamId());
        if (team != null) {
            Label teamLabel = new Label("Team: " + team.getName());
            teamLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
            card.getChildren().add(teamLabel);
        }
        
        // Description
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            Label descLabel = new Label(project.getDescription());
            descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555; -fx-wrap-text: true;");
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(Double.MAX_VALUE);
            card.getChildren().add(descLabel);
        }
        
        // Status badge
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label statusBadge = new Label(project.getStatus());
        String statusColor = switch (project.getStatus()) {
            case "Active" -> "#10B981";
            case "Completed" -> "#6366F1";
            case "On Hold" -> "#F59E0B";
            default -> "#6B7280";
        };
        statusBadge.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; " +
                           "-fx-font-size: 11px; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-weight: bold;");
        statusBox.getChildren().add(statusBadge);
        
        // Dates
        if (project.getStartDate() != null || project.getEndDate() != null) {
            Label datesLabel = new Label(
                (project.getStartDate() != null ? "Start: " + project.getStartDate() : "") +
                (project.getStartDate() != null && project.getEndDate() != null ? " | " : "") +
                (project.getEndDate() != null ? "End: " + project.getEndDate() : "")
            );
            datesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
            statusBox.getChildren().add(datesLabel);
        }
        
        card.getChildren().add(statusBox);
        
        return card;
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
