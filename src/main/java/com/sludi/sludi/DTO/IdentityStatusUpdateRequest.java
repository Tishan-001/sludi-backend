package com.sludi.sludi.DTO;

public class IdentityStatusUpdateRequest {
    private String status; // Active, Suspended, Revoked
    private String reason;
    private String updatedBy;

    // Constructors
    public IdentityStatusUpdateRequest() {}

    public IdentityStatusUpdateRequest(String status, String reason, String updatedBy) {
        this.status = status;
        this.reason = reason;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
