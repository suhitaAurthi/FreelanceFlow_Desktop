package com.example.freelanceflow_desktop;

public class ServiceRequest {
    private int id;
    private int serviceId;
    private int clientId;
    private String status; // Pending, Approved, Rejected
    private String requestDate;
    private String responseDate;
    
    public ServiceRequest(int id, int serviceId, int clientId, String status, String requestDate, String responseDate) {
        this.id = id;
        this.serviceId = serviceId;
        this.clientId = clientId;
        this.status = status;
        this.requestDate = requestDate;
        this.responseDate = responseDate;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    
    public int getClientId() {
        return clientId;
    }
    
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
    
    public String getResponseDate() {
        return responseDate;
    }
    
    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }
}
