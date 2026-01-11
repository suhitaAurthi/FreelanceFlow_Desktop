package com.example.freelanceflow_desktop;

public class RecruitmentPost {
    private int id;
    private int teamId;
    private String title;
    private String description;
    private String requiredSkills;
    private int positions;
    private String status;
    private int createdBy;
    private String createdDate;

    public RecruitmentPost() {
    }

    public RecruitmentPost(int teamId, String title, String description, String requiredSkills, int positions, int createdBy) {
        this.teamId = teamId;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.positions = positions;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getPositions() {
        return positions;
    }

    public void setPositions(int positions) {
        this.positions = positions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
