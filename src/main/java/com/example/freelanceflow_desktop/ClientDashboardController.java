package com.example.freelanceflow_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ClientDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImageView;
    @FXML private Button servicesBtn;
    @FXML private VBox postJobView;
    @FXML private VBox myJobsView;
    @FXML private VBox teamsView;
    @FXML private VBox servicesView;
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
    @FXML private VBox servicesList;
    @FXML private VBox applicationsList;

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
        
        // Load initial data
        loadMyJobs();
        loadTeams();
    }
    
    private void loadProfilePhoto(int userId) {
        if (profileImageView == null) {
            System.err.println("ProfileImageView is not initialized");
            return;
        }
        
        String photoPath = ClientDetailsDAO.getProfilePhotoPath(userId);
        
        if (photoPath != null && !photoPath.isEmpty()) {
            File photoFile = new File(photoPath);
            if (photoFile.exists()) {
                try (FileInputStream fis = new FileInputStream(photoFile)) {
                    Image image = new Image(fis);
                    profileImageView.setImage(image);
                    
                    // Make it circular
                    Circle clip = new Circle(20, 20, 20);
                    profileImageView.setClip(clip);
                } catch (Exception e) {
                    System.err.println("Error loading image file: " + e.getMessage());
                }
            }
        }
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
    
    @FXML
    protected void showServicesView() {
        hideAllViews();
        servicesView.setVisible(true);
        loadServices();
    }

    private void hideAllViews() {
        postJobView.setVisible(false);
        myJobsView.setVisible(false);
        teamsView.setVisible(false);
        servicesView.setVisible(false);
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
            showAlert("Error", "Please fill in all required fields (Title, Description, Budget)!", Alert.AlertType.ERROR);
            return;
        }

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user session found!", Alert.AlertType.ERROR);
            return;
        }

        // Create job object
        Job job = new Job(currentUser.getId(), title, description, budget, duration, skills);
        
        // Save to database
        boolean success = JobDAO.createJob(job);
        
        if (success) {
            showAlert("Success", "Job posted successfully! Teams can now see and apply for this position.", Alert.AlertType.INFORMATION);
            
            // Clear fields
            jobTitleField.clear();
            jobDescriptionArea.clear();
            jobBudgetField.clear();
            jobDurationField.clear();
            jobSkillsField.clear();
            
            // Refresh job list
            loadMyJobs();
        } else {
            showAlert("Error", "Failed to post job. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void loadMyJobs() {
        myJobsList.getChildren().clear();
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        List<Job> jobs = JobDAO.getJobsByClientId(currentUser.getId());
        
        if (jobs.isEmpty()) {
            Label placeholder = new Label("No jobs posted yet. Click 'Post Job' to create one!");
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            myJobsList.getChildren().add(placeholder);
        } else {
            for (Job job : jobs) {
                VBox jobCard = createJobCard(job);
                myJobsList.getChildren().add(jobCard);
            }
        }
    }
    
    private VBox createJobCard(Job job) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                     "-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        
        // Header with title and status
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text titleText = new Text(job.getTitle());
        titleText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #1E3A8A;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(job.getStatus());
        String statusColor = switch(job.getStatus()) {
            case "Open" -> "#10B981";
            case "In Progress" -> "#F59E0B";
            case "Completed" -> "#6366F1";
            case "Closed" -> "#EF4444";
            default -> "#6B7280";
        };
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; " +
                           "-fx-padding: 5 12; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        header.getChildren().addAll(titleText, spacer, statusLabel);
        
        // Description
        Label descLabel = new Label(job.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        
        // Details section
        HBox details = new HBox(20);
        details.setAlignment(Pos.CENTER_LEFT);
        
        VBox budgetBox = new VBox(3);
        Label budgetLabel = new Label("Budget:");
        budgetLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label budgetValue = new Label(job.getBudget());
        budgetValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #10B981;");
        budgetBox.getChildren().addAll(budgetLabel, budgetValue);
        
        VBox durationBox = new VBox(3);
        Label durationLabel = new Label("Duration:");
        durationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label durationValue = new Label(job.getDuration() != null && !job.getDuration().isEmpty() ? 
                                        job.getDuration() : "Not specified");
        durationValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #333;");
        durationBox.getChildren().addAll(durationLabel, durationValue);
        
        details.getChildren().addAll(budgetBox, durationBox);
        
        // Skills
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            Label skillsLabel = new Label("Required Skills: " + job.getSkills());
            skillsLabel.setWrapText(true);
            skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1E3A8A; " +
                               "-fx-background-color: #EBF5FF; -fx-padding: 8; -fx-background-radius: 6;");
            card.getChildren().addAll(header, descLabel, details, skillsLabel);
        } else {
            card.getChildren().addAll(header, descLabel, details);
        }
        
        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Button viewAppsBtn = new Button("View Applications");
        viewAppsBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                            "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        viewAppsBtn.setOnAction(e -> showApplicationsDialog(job));
        
        Button changeStatusBtn = new Button("Change Status");
        changeStatusBtn.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; " +
                                "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        changeStatusBtn.setOnAction(e -> showChangeStatusDialog(job));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; " +
                          "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deleteJob(job));
        
        actions.getChildren().addAll(viewAppsBtn, changeStatusBtn, deleteBtn);
        card.getChildren().add(actions);
        
        return card;
    }
    
    private void showApplicationsDialog(Job job) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Job Applications");
        dialog.setHeaderText("Applications for: " + job.getTitle());
        
        // Fetch applications
        java.util.List<JobApplication> applications = JobApplicationDAO.getApplicationsByJobId(job.getId());
        
        VBox content = new VBox(15);
        content.setPrefWidth(550);
        content.setMaxHeight(400);
        
        if (applications.isEmpty()) {
            Label noApps = new Label("No applications yet.");
            noApps.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            content.getChildren().add(noApps);
        } else {
            ScrollPane scroll = new ScrollPane();
            VBox appsList = new VBox(12);
            appsList.setStyle("-fx-padding: 10;");
            
            for (JobApplication app : applications) {
                VBox appCard = new VBox(10);
                appCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #E0E0E0; " +
                               "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1;");
                
                // Get team details
                Team team = TeamDAO.getTeamById(app.getTeamId());
                if (team == null) continue;
                
                Label teamName = new Label("Team: " + team.getName());
                teamName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
                
                Label teamDesc = new Label(team.getDescription());
                teamDesc.setWrapText(true);
                teamDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
                
                Label teamSkills = new Label("Skills: " + (team.getSkills() != null ? team.getSkills() : "Not specified"));
                teamSkills.setWrapText(true);
                teamSkills.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-background-color: #F3F4F6; " +
                                  "-fx-padding: 6; -fx-background-radius: 4;");
                
                Label appliedDate = new Label("Applied: " + app.getAppliedDate());
                appliedDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
                
                Label statusLabel = new Label("Status: " + app.getStatus());
                String statusColor = app.getStatus().equals("Pending") ? "#F59E0B" : 
                                    app.getStatus().equals("Approved") ? "#10B981" : "#EF4444";
                statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; " +
                                   "-fx-padding: 4 10; -fx-background-radius: 10; -fx-font-size: 12px; -fx-font-weight: bold;");
                
                appCard.getChildren().addAll(teamName, teamDesc, teamSkills, appliedDate, statusLabel);
                
                // Add approve button only for pending applications
                if (app.getStatus().equals("Pending")) {
                    Button approveBtn = new Button("✓ Approve & Create Project");
                    approveBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                                      "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
                    approveBtn.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Approve Application");
                        confirm.setHeaderText("Approve " + team.getName() + "?");
                        confirm.setContentText("This will:\n" +
                                             "• Approve this application\n" +
                                             "• Create a new project for the team\n" +
                                             "• Notify all team members\n" +
                                             "• Update job status to 'In Progress'");
                        
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                System.out.println("Approving application: appId=" + app.getId() + 
                                                 ", jobId=" + job.getId() + ", teamId=" + app.getTeamId());
                                
                                boolean success = JobApplicationDAO.approveApplication(
                                    app.getId(), job.getId(), app.getTeamId()
                                );
                                
                                System.out.println("Approval result: " + success);
                                
                                if (success) {
                                    showAlert("Success", 
                                             "Application approved! Project created and team notified.", 
                                             Alert.AlertType.INFORMATION);
                                    dialog.close();
                                    loadMyJobs(); // Refresh jobs list
                                } else {
                                    showAlert("Error", 
                                             "Failed to approve application. Please check the console for details.", 
                                             Alert.AlertType.ERROR);
                                }
                            }
                        });
                    });
                    appCard.getChildren().add(approveBtn);
                }
                
                appsList.getChildren().add(appCard);
            }
            
            scroll.setContent(appsList);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            content.getChildren().add(scroll);
        }
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void showChangeStatusDialog(Job job) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Open", "Open", "In Progress", "Completed", "Closed");
        dialog.setTitle("Change Job Status");
        dialog.setHeaderText("Update status for: " + job.getTitle());
        dialog.setContentText("Select new status:");
        
        dialog.showAndWait().ifPresent(newStatus -> {
            if (JobDAO.updateJobStatus(job.getId(), newStatus)) {
                showAlert("Success", "Job status updated successfully!", Alert.AlertType.INFORMATION);
                loadMyJobs();
            } else {
                showAlert("Error", "Failed to update job status.", Alert.AlertType.ERROR);
            }
        });
    }
    
    private void deleteJob(Job job) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Job");
        confirmDialog.setHeaderText("Are you sure you want to delete this job?");
        confirmDialog.setContentText(job.getTitle());
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (JobDAO.deleteJob(job.getId())) {
                    showAlert("Success", "Job deleted successfully!", Alert.AlertType.INFORMATION);
                    loadMyJobs();
                } else {
                    showAlert("Error", "Failed to delete job.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void loadTeams() {
        teamsList.getChildren().clear();
        
        List<Team> teams = TeamDAO.getAllTeams();
        
        if (teams.isEmpty()) {
            Label placeholder = new Label("No teams available yet.");
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            teamsList.getChildren().add(placeholder);
        } else {
            for (Team team : teams) {
                VBox teamCard = createTeamCard(team);
                teamsList.getChildren().add(teamCard);
            }
        }
    }
    
    private VBox createTeamCard(Team team) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                     "-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        
        // Team name
        Text nameText = new Text(team.getName());
        nameText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #1E3A8A;");
        
        // Description
        Label descLabel = new Label(team.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        
        // Skills
        if (team.getSkills() != null && !team.getSkills().isEmpty()) {
            Label skillsLabel = new Label("Skills: " + team.getSkills());
            skillsLabel.setWrapText(true);
            skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1E3A8A; " +
                               "-fx-background-color: #EBF5FF; -fx-padding: 8; -fx-background-radius: 6;");
            card.getChildren().addAll(nameText, descLabel, skillsLabel);
        } else {
            card.getChildren().addAll(nameText, descLabel);
        }
        
        // Member count
        int memberCount = TeamDAO.getTeamMemberCount(team.getId());
        Label memberLabel = new Label(memberCount + " member" + (memberCount != 1 ? "s" : ""));
        memberLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        card.getChildren().add(memberLabel);
        
        // View Members button
        Button viewMembersBtn = new Button("View Members");
        viewMembersBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                               "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
        viewMembersBtn.setOnAction(e -> showTeamMembersDialog(team));
        card.getChildren().add(viewMembersBtn);
        
        return card;
    }
    
    private void showTeamMembersDialog(Team team) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Team Members");
        dialog.setHeaderText(team.getName() + " - Members");
        
        VBox content = new VBox(15);
        content.setPrefWidth(550);
        content.setMaxHeight(500);
        
        List<User> members = TeamDAO.getTeamMembers(team.getId());
        
        if (members.isEmpty()) {
            Label noMembers = new Label("This team has no members yet.");
            noMembers.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            content.getChildren().add(noMembers);
        } else {
            ScrollPane scroll = new ScrollPane();
            VBox membersList = new VBox(12);
            membersList.setStyle("-fx-padding: 10;");
            
            for (User member : members) {
                VBox memberCard = new VBox(10);
                memberCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #E0E0E0; " +
                                  "-fx-border-radius: 8; -fx-background-radius: 8; -fx-border-width: 1;");
                
                HBox header = new HBox(15);
                header.setAlignment(Pos.CENTER_LEFT);
                
                // Profile photo
                javafx.scene.image.ImageView profileImg = new javafx.scene.image.ImageView();
                profileImg.setFitHeight(45);
                profileImg.setFitWidth(45);
                profileImg.setPreserveRatio(true);
                
                try {
                    String photoPath = FreelancerDetailsDAO.getProfilePhotoPath(member.getId());
                    if (photoPath != null && !photoPath.isEmpty()) {
                        File photoFile = new File(photoPath);
                        if (photoFile.exists()) {
                            try (FileInputStream fis = new FileInputStream(photoFile)) {
                                Image image = new Image(fis);
                                profileImg.setImage(image);
                                Circle clip = new Circle(22.5, 22.5, 22.5);
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
                
                Label emailLabel = new Label("📧 " + member.getEmail());
                emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
                
                nameBox.getChildren().addAll(nameLabel, emailLabel);
                header.getChildren().addAll(profileImg, nameBox);
                
                memberCard.getChildren().add(header);
                
                // Get freelancer details
                try (java.sql.Connection conn = DatabaseManager.getConnection();
                     java.sql.PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT display_name, phone, location, rate_per_hour FROM freelancer_details WHERE user_id = ?")) {
                    
                    pstmt.setInt(1, member.getId());
                    java.sql.ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        VBox detailsBox = new VBox(6);
                        detailsBox.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 10; -fx-background-radius: 6;");
                        
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
                            memberCard.getChildren().add(detailsBox);
                        }
                    }
                } catch (java.sql.SQLException e) {
                    System.err.println("Error loading member details: " + e.getMessage());
                }
                
                membersList.getChildren().add(memberCard);
            }
            
            scroll.setContent(membersList);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            content.getChildren().add(scroll);
        }
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void loadApplications() {
        applicationsList.getChildren().clear();
        
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        // Get all jobs posted by this client
        List<Job> myJobs = JobDAO.getJobsByClientId(currentUser.getId());
        
        if (myJobs.isEmpty()) {
            VBox emptyState = new VBox(15);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12;");
            
            Label titleLabel = new Label("No Job Applications Yet");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            
            Label msgLabel = new Label("Post a job to start receiving applications from teams!");
            msgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            msgLabel.setWrapText(true);
            
            emptyState.getChildren().addAll(titleLabel, msgLabel);
            applicationsList.getChildren().add(emptyState);
            return;
        }
        
        // For each job, get applications and display them
        int totalApplications = 0;
        for (Job job : myJobs) {
            List<JobApplication> jobApps = JobApplicationDAO.getApplicationsByJobId(job.getId());
            
            if (!jobApps.isEmpty()) {
                totalApplications += jobApps.size();
                
                // Job header
                VBox jobSection = new VBox(10);
                jobSection.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                                   "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
                
                Label jobTitle = new Label("📋 " + job.getTitle());
                jobTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
                jobSection.getChildren().add(jobTitle);
                
                Label jobStatus = new Label("Status: " + job.getStatus() + " | Applications: " + jobApps.size());
                jobStatus.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
                jobSection.getChildren().add(jobStatus);
                
                // Applications for this job
                VBox appsContainer = new VBox(10);
                appsContainer.setStyle("-fx-padding: 10 0 0 20;");
                
                for (JobApplication app : jobApps) {
                    appsContainer.getChildren().add(createApplicationCard(app, job));
                }
                
                jobSection.getChildren().add(appsContainer);
                applicationsList.getChildren().add(jobSection);
            }
        }
        
        if (totalApplications == 0) {
            VBox emptyState = new VBox(15);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12;");
            
            Label titleLabel = new Label("No Applications Yet");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            
            Label msgLabel = new Label("Teams will appear here when they apply for your job postings.");
            msgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            
            emptyState.getChildren().addAll(titleLabel, msgLabel);
            applicationsList.getChildren().add(emptyState);
        }
    }
    
    private VBox createApplicationCard(JobApplication app, Job job) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 15; -fx-background-radius: 8; " +
                     "-fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // Get team details
        Team team = TeamDAO.getTeamById(app.getTeamId());
        if (team == null) {
            return card;
        }
        
        // Team name
        Label teamName = new Label("👥 " + team.getName());
        teamName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        card.getChildren().add(teamName);
        
        // Team details
        if (team.getDescription() != null && !team.getDescription().isEmpty()) {
            Label teamDesc = new Label(team.getDescription());
            teamDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            teamDesc.setWrapText(true);
            teamDesc.setMaxWidth(600);
            card.getChildren().add(teamDesc);
        }
        
        // Skills
        if (team.getSkills() != null && !team.getSkills().isEmpty()) {
            Label skillsLabel = new Label("Skills: " + team.getSkills());
            skillsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-style: italic;");
            card.getChildren().add(skillsLabel);
        }
        
        // Team size
        int memberCount = TeamDAO.getTeamMembers(app.getTeamId()).size();
        Label memberLabel = new Label("Team Size: " + memberCount + " members");
        memberLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        card.getChildren().add(memberLabel);
        
        // Application info
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        Label appliedDate = new Label("Applied: " + app.getAppliedDate());
        appliedDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
        statusBox.getChildren().add(appliedDate);
        
        // Status badge
        Label statusBadge = new Label(app.getStatus());
        String badgeColor = switch (app.getStatus()) {
            case "Pending" -> "#F59E0B";
            case "Approved" -> "#10B981";
            case "Rejected" -> "#EF4444";
            default -> "#6B7280";
        };
        statusBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; " +
                           "-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 10; -fx-font-weight: bold;");
        statusBox.getChildren().add(statusBadge);
        
        card.getChildren().add(statusBox);
        
        // Action buttons for pending applications
        if ("Pending".equals(app.getStatus())) {
            HBox actionBox = new HBox(10);
            actionBox.setAlignment(Pos.CENTER_LEFT);
            actionBox.setStyle("-fx-padding: 5 0 0 0;");
            
            Button viewTeamBtn = new Button("View Team Details");
            viewTeamBtn.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white; " +
                               "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            viewTeamBtn.setOnAction(e -> showTeamDetailsDialog(team));
            
            Button approveBtn = new Button("✓ Approve & Create Project");
            approveBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                              "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;");
            approveBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Approve Application");
                confirm.setHeaderText("Approve " + team.getName() + "?");
                confirm.setContentText("This will:\\n" +
                                     "• Approve this application\\n" +
                                     "• Create a new project for the team\\n" +
                                     "• Notify all team members\\n" +
                                     "• Update job status to 'In Progress'");
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        System.out.println("Approving application: appId=" + app.getId() + 
                                         ", jobId=" + job.getId() + ", teamId=" + app.getTeamId());
                        
                        boolean success = JobApplicationDAO.approveApplication(
                            app.getId(), job.getId(), app.getTeamId()
                        );
                        
                        System.out.println("Approval result: " + success);
                        
                        if (success) {
                            showAlert("Success", 
                                     "Application approved! Project created and team notified.", 
                                     Alert.AlertType.INFORMATION);
                            loadApplications(); // Refresh applications list
                            loadMyJobs(); // Refresh jobs list
                        } else {
                            showAlert("Error", 
                                     "Failed to approve application. Please check the console for details.", 
                                     Alert.AlertType.ERROR);
                        }
                    }
                });
            });
            
            Button rejectBtn = new Button("✗ Reject");
            rejectBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; " +
                             "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            rejectBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Reject Application");
                confirm.setHeaderText("Reject " + team.getName() + "'s application?");
                confirm.setContentText("This action cannot be undone.");
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean success = JobApplicationDAO.rejectApplication(app.getId());
                        if (success) {
                            // Notify team admin
                            Team teamData = TeamDAO.getTeamById(app.getTeamId());
                            if (teamData != null) {
                                NotificationDAO.createNotification(
                                    teamData.getAdminId(),
                                    "application_rejected",
                                    "Your application for '" + job.getTitle() + "' was not accepted.",
                                    String.valueOf(app.getId())
                                );
                            }
                            
                            showAlert("Success", "Application rejected.", Alert.AlertType.INFORMATION);
                            loadApplications(); // Refresh
                        } else {
                            showAlert("Error", "Failed to reject application.", Alert.AlertType.ERROR);
                        }
                    }
                });
            });
            
            actionBox.getChildren().addAll(viewTeamBtn, approveBtn, rejectBtn);
            card.getChildren().add(actionBox);
        } else if ("Approved".equals(app.getStatus())) {
            Label approvedLabel = new Label("✓ Project created and team notified");
            approvedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
            card.getChildren().add(approvedLabel);
        }
        
        return card;
    }
    
    private void showTeamDetailsDialog(Team team) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Team Details");
        dialog.setHeaderText(team.getName());
        
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        content.setPrefWidth(500);
        
        // Team info
        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        content.getChildren().add(descLabel);
        
        Label descValue = new Label(team.getDescription() != null ? team.getDescription() : "No description");
        descValue.setWrapText(true);
        descValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        content.getChildren().add(descValue);
        
        // Skills
        Label skillsLabel = new Label("Skills:");
        skillsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        content.getChildren().add(skillsLabel);
        
        Label skillsValue = new Label(team.getSkills() != null ? team.getSkills() : "Not specified");
        skillsValue.setWrapText(true);
        skillsValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        content.getChildren().add(skillsValue);
        
        // Team members
        Label membersLabel = new Label("Team Members:");
        membersLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        content.getChildren().add(membersLabel);
        
        List<User> members = TeamDAO.getTeamMembers(team.getId());
        VBox membersBox = new VBox(5);
        for (User member : members) {
            Label memberLabel = new Label("• " + member.getFirstName() + " " + member.getLastName() + " (" + member.getRole() + ")");
            memberLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            membersBox.getChildren().add(memberLabel);
        }
        content.getChildren().add(membersBox);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
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
    
    private void loadServices() {
        servicesList.getChildren().clear();
        
        List<ServicePost> services = ServicePostDAO.getAllActiveServicePosts();
        
        if (services.isEmpty()) {
            Label placeholder = new Label("No services available at the moment.");
            placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
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
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                     "-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        
        // Team name header
        String teamName = ServicePostDAO.getTeamName(service.getTeamId());
        Label teamLabel = new Label("By: " + teamName);
        teamLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-style: italic;");
        
        // Service title
        Text titleText = new Text(service.getTitle());
        titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1E3A8A;");
        
        // Description
        Label descLabel = new Label(service.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        
        card.getChildren().addAll(teamLabel, titleText, descLabel);
        
        // Details section
        HBox details = new HBox(20);
        details.setAlignment(Pos.CENTER_LEFT);
        details.setStyle("-fx-padding: 10 0;");
        
        if (service.getCategory() != null && !service.getCategory().isEmpty()) {
            VBox categoryBox = new VBox(3);
            Label catLabel = new Label("Category:");
            catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
            Label catValue = new Label(service.getCategory());
            catValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #333;");
            categoryBox.getChildren().addAll(catLabel, catValue);
            details.getChildren().add(categoryBox);
        }
        
        if (service.getPricing() != null && !service.getPricing().isEmpty()) {
            VBox priceBox = new VBox(3);
            Label priceLabel = new Label("Pricing:");
            priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
            Label priceValue = new Label(service.getPricing());
            priceValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
            priceBox.getChildren().addAll(priceLabel, priceValue);
            details.getChildren().add(priceBox);
        }
        
        if (service.getDeliveryTime() != null && !service.getDeliveryTime().isEmpty()) {
            VBox deliveryBox = new VBox(3);
            Label deliveryLabel = new Label("Delivery:");
            deliveryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
            Label deliveryValue = new Label(service.getDeliveryTime());
            deliveryValue.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #333;");
            deliveryBox.getChildren().addAll(deliveryLabel, deliveryValue);
            details.getChildren().add(deliveryBox);
        }
        
        if (!details.getChildren().isEmpty()) {
            card.getChildren().add(details);
        }
        
        // Check if already requested
        User currentUser = SessionManager.getCurrentUser();
        boolean hasRequested = currentUser != null && 
                             ServiceRequestDAO.hasRequested(service.getId(), currentUser.getId());
        
        // Request collaboration button
        Button requestBtn = new Button(hasRequested ? "✓ Request Sent" : "Request Collaboration");
        if (hasRequested) {
            requestBtn.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; " +
                               "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 24; " +
                               "-fx-background-radius: 8; -fx-cursor: default;");
            requestBtn.setDisable(true);
        } else {
            requestBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                               "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 24; " +
                               "-fx-background-radius: 8; -fx-cursor: hand;");
            requestBtn.setOnAction(e -> requestCollaboration(service, teamName, requestBtn));
        }
        
        HBox buttonBox = new HBox(requestBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        card.getChildren().add(buttonBox);
        
        return card;
    }
    
    private void requestCollaboration(ServicePost service, String teamName, Button requestBtn) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Request Collaboration");
        confirmDialog.setHeaderText("Request collaboration for: " + service.getTitle());
        confirmDialog.setContentText("Team: " + teamName + "\n\n" +
                                    "Send a collaboration request to this team?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                User currentUser = SessionManager.getCurrentUser();
                if (currentUser == null) return;
                
                // Create service request record
                int requestId = ServiceRequestDAO.createServiceRequest(service.getId(), currentUser.getId());
                
                if (requestId > 0) {
                    // Get team admin
                    Team team = TeamDAO.getTeamById(service.getTeamId());
                    if (team != null) {
                        // Send notification to team admin
                        String clientName = currentUser.getFirstName() + " " + currentUser.getLastName();
                        String message = "Client \"" + clientName + 
                                       "\" has requested collaboration for your service: " + service.getTitle();
                        NotificationDAO.createNotification(team.getAdminId(), "service_request", 
                                                          message, String.valueOf(requestId));
                        
                        // Update button
                        requestBtn.setText("✓ Request Sent");
                        requestBtn.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; " +
                                          "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 24; " +
                                          "-fx-background-radius: 8; -fx-cursor: default;");
                        requestBtn.setDisable(true);
                        
                        showAlert("Success", 
                                "Your collaboration request has been sent! The team will review and contact you.", 
                                Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to send request. Please try again.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Error", "Failed to create request. Please try again.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
