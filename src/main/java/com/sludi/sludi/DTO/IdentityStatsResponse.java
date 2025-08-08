package com.sludi.sludi.DTO;

public class IdentityStatsResponse {
    private long totalIdentities;
    private long activeIdentities;
    private long suspendedIdentities;
    private long revokedIdentities;
    private long recentRegistrations;
    private long recentVerifications;
    private String reportGeneratedAt;

    // Constructors
    public IdentityStatsResponse() {
        this.reportGeneratedAt = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public long getTotalIdentities() { return totalIdentities; }
    public void setTotalIdentities(long totalIdentities) { this.totalIdentities = totalIdentities; }

    public long getActiveIdentities() { return activeIdentities; }
    public void setActiveIdentities(long activeIdentities) { this.activeIdentities = activeIdentities; }

    public long getSuspendedIdentities() { return suspendedIdentities; }
    public void setSuspendedIdentities(long suspendedIdentities) { this.suspendedIdentities = suspendedIdentities; }

    public long getRevokedIdentities() { return revokedIdentities; }
    public void setRevokedIdentities(long revokedIdentities) { this.revokedIdentities = revokedIdentities; }

    public long getRecentRegistrations() { return recentRegistrations; }
    public void setRecentRegistrations(long recentRegistrations) { this.recentRegistrations = recentRegistrations; }

    public long getRecentVerifications() { return recentVerifications; }
    public void setRecentVerifications(long recentVerifications) { this.recentVerifications = recentVerifications; }

    public String getReportGeneratedAt() { return reportGeneratedAt; }
    public void setReportGeneratedAt(String reportGeneratedAt) { this.reportGeneratedAt = reportGeneratedAt; }
}
