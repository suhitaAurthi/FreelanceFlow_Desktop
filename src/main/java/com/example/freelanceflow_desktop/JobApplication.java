package com.example.freelanceflow_desktop;

public class JobApplication {
    private int id;
    private int jobId;
    private int teamId;
    private String status; // Pending, Approved, Rejected
    private String appliedDate;
    private String responseDate;
    
    public JobApplication(int id, int jobId, int teamId, String status, String appliedDate, String responseDate) {
        this.id = id;
        this.jobId = jobId;
        this.teamId = teamId;
        this.status = status;
        this.appliedDate = appliedDate;
        this.responseDate = responseDate;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getJobId() {
        return jobId;
    }
    
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
    
    public int getTeamId() {
        return teamId;
    }
    
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }
    
    public String getResponseDate() {
        return responseDate;
    }
    
    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }
}
