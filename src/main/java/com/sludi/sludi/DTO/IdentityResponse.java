package com.sludi.sludi.DTO;

public class IdentityResponse {
    private boolean success;
    private String message;
    private String nic;
    private String status;
    private Object data;
    private String timestamp;

    // Constructors
    public IdentityResponse() {}

    public IdentityResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}