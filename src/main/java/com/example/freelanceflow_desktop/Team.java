package com.example.freelanceflow_desktop;

public class Team {
    private int id;
    private String name;
    private String description;
    private String skills;
    private int maxMembers;
    private int adminId;
    private String createdDate;

    public Team() {}

    public Team(int id, String name, String description, String skills, int maxMembers, int adminId, String createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.skills = skills;
        this.maxMembers = maxMembers;
        this.adminId = adminId;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
