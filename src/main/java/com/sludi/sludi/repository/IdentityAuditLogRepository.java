package com.sludi.sludi.repository;

import com.sludi.sludi.domain.IdentityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IdentityAuditLogRepository extends JpaRepository<IdentityAuditLog, Long> {

    List<IdentityAuditLog> findByNic(String nic);

    List<IdentityAuditLog> findByNicOrderByTimestampDesc(String nic);

    List<IdentityAuditLog> findByAction(String action);

    List<IdentityAuditLog> findByPerformedBy(String performedBy);

    List<IdentityAuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM IdentityAuditLog a WHERE a.nic = :nic AND a.action = :action ORDER BY a.timestamp DESC")
    List<IdentityAuditLog> findByNicAndActionOrderByTimestampDesc(@Param("nic") String nic, @Param("action") String action);

    @Query("SELECT a FROM IdentityAuditLog a WHERE a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<IdentityAuditLog> findRecentActivities(@Param("since") LocalDateTime since);
}