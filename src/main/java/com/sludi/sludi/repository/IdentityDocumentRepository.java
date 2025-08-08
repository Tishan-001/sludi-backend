package com.sludi.sludi.repository;

import com.sludi.sludi.domain.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Long> {

    List<IdentityDocument> findByNic(String nic);

    List<IdentityDocument> findByNicAndDocumentType(String nic, String documentType);

    Optional<IdentityDocument> findByIpfsHash(String ipfsHash);

    List<IdentityDocument> findByVerified(Boolean verified);

    @Query("SELECT d FROM IdentityDocument d WHERE d.nic = :nic AND d.verified = true")
    List<IdentityDocument> findVerifiedDocumentsByNic(@Param("nic") String nic);

    @Query("SELECT COUNT(d) FROM IdentityDocument d WHERE d.nic = :nic")
    long countDocumentsByNic(@Param("nic") String nic);
}