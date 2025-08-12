package com.sludi.sludi.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "identity_audit_log")
public class IdentityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nic", length = 12, nullable = false)
    private String nic;

    @Column(name = "action", length = 50, nullable = false)
    private String action; // CREATE, UPDATE, VERIFY, SUSPEND, etc.

    @Column(name = "performed_by", length = 100, nullable = false)
    private String performedBy; // User/System who performed the action

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "details", length = 1000)
    private String details; // Additional details about the action

    @Column(name = "blockchain_tx_id", length = 100)
    private String blockchainTxId; // Reference to blockchain transaction

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    // Constructors
    public IdentityAuditLog() {}

    public IdentityAuditLog(String nic, String action, String performedBy, String details) {
        this.nic = nic;
        this.action = action;
        this.performedBy = performedBy;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getBlockchainTxId() { return blockchainTxId; }
    public void setBlockchainTxId(String blockchainTxId) { this.blockchainTxId = blockchainTxId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}