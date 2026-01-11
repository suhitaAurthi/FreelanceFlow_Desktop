package com.example.freelanceflow_desktop;

public class ServicePost {
    private int id;
    private int teamId;
    private String title;
    private String description;
    private String category;
    private String pricing;
    private String deliveryTime;
    private String status; // "Active", "Inactive", "Closed"
    private int createdBy;
    private String createdDate;
    
    public ServicePost() {
        this.status = "Active";
    }
    
    public ServicePost(int teamId, String title, String description, String category, 
                      String pricing, String deliveryTime, int createdBy) {
        this.teamId = teamId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.pricing = pricing;
        this.deliveryTime = deliveryTime;
        this.createdBy = createdBy;
        this.status = "Active";
    }
    
    // Getters and Setters
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPricing() {
        return pricing;
    }
    
    public void setPricing(String pricing) {
        this.pricing = pricing;
    }
    
    public String getDeliveryTime() {
        return deliveryTime;
    }
    
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
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
