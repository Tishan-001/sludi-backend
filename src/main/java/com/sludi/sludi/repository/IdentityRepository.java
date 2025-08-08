package com.sludi.sludi.repository;

import com.sludi.sludi.domain.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, String> {

    Optional<Identity> findByNic(String nic);

    List<Identity> findByStatus(String status);

    List<Identity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT i FROM Identity i WHERE i.status = 'Active'")
    List<Identity> findAllActiveIdentities();

    @Query("SELECT COUNT(i) FROM Identity i WHERE i.status = :status")
    long countByStatus(@Param("status") String status);

    boolean existsByNic(String nic);
}
