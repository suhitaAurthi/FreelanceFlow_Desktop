package com.example.freelanceflow_desktop;

public class Job {
    private int id;
    private int clientId;
    private String title;
    private String description;
    private String budget;
    private String duration;
    private String skills;
    private String status; // "Open", "In Progress", "Completed", "Closed"
    private String postedDate;
    
    public Job() {
        this.status = "Open";
    }
    
    public Job(int clientId, String title, String description, String budget, String duration, String skills) {
        this.clientId = clientId;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.duration = duration;
        this.skills = skills;
        this.status = "Open";
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getClientId() {
        return clientId;
    }
    
    public void setClientId(int clientId) {
        this.clientId = clientId;
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
    
    public String getBudget() {
        return budget;
    }
    
    public void setBudget(String budget) {
        this.budget = budget;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getSkills() {
        return skills;
    }
    
    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPostedDate() {
        return postedDate;
    }
    
    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
}
