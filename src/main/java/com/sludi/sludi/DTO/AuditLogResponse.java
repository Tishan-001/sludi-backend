package com.sludi.sludi.DTO;

public class AuditLogResponse {
    private Long id;
    private String nic;
    private String action;
    private String performedBy;
    private String timestamp;
    private String details;
    private String blockchainTxId;

    // Constructors
    public AuditLogResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getBlockchainTxId() { return blockchainTxId; }
    public void setBlockchainTxId(String blockchainTxId) { this.blockchainTxId = blockchainTxId; }
}