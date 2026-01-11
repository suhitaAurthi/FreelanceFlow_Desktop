package com.example.freelanceflow_desktop;

public class TeamInvitation {
    private int id;
    private int teamId;
    private int userId;
    private int invitedBy;
    private String status;
    private String invitedDate;
    private String responseDate;

    public TeamInvitation() {
    }

    public TeamInvitation(int teamId, int userId, int invitedBy, String status) {
        this.teamId = teamId;
        this.userId = userId;
        this.invitedBy = invitedBy;
        this.status = status;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(int invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvitedDate() {
        return invitedDate;
    }

    public void setInvitedDate(String invitedDate) {
        this.invitedDate = invitedDate;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }
}
