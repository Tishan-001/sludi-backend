package com.sludi.sludi.service;

import com.sludi.sludi.domain.IdentityAuditLog;
import com.sludi.sludi.repository.IdentityAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private IdentityAuditLogRepository auditLogRepository;

    /**
     * Log an identity-related action
     */
    public void logAction(String nic, String action, String performedBy, String details, String ipAddress) {
        try {
            IdentityAuditLog auditLog = new IdentityAuditLog();
            auditLog.setNic(nic);
            auditLog.setAction(action);
            auditLog.setPerformedBy(performedBy);
            auditLog.setDetails(details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Ensure audit logging doesn't break main functionality
            System.err.println("Failed to log audit action: " + e.getMessage());
        }
    }

    /**
     * Log an action with blockchain transaction ID
     */
    public void logActionWithTxId(String nic, String action, String performedBy, String details,
                                  String ipAddress, String blockchainTxId) {
        try {
            IdentityAuditLog auditLog = new IdentityAuditLog();
            auditLog.setNic(nic);
            auditLog.setAction(action);
            auditLog.setPerformedBy(performedBy);
            auditLog.setDetails(details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setBlockchainTxId(blockchainTxId);
            auditLog.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            System.err.println("Failed to log audit action: " + e.getMessage());
        }
    }

    /**
     * Get audit log for a specific NIC
     */
    public List<IdentityAuditLog> getAuditLogByNic(String nic) {
        return auditLogRepository.findByNicOrderByTimestampDesc(nic);
    }

    /**
     * Get recent activities across all identities
     */
    public List<IdentityAuditLog> getRecentActivities(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findRecentActivities(since);
    }

    /**
     * Get audit log by action type
     */
    public List<IdentityAuditLog> getAuditLogByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    /**
     * Get audit log by performer
     */
    public List<IdentityAuditLog> getAuditLogByPerformer(String performedBy) {
        return auditLogRepository.findByPerformedBy(performedBy);
    }

    /**
     * Get audit log within a date range
     */
    public List<IdentityAuditLog> getAuditLogByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
}