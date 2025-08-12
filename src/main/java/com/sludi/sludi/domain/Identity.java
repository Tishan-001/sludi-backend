package com.sludi.sludi.domain;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "identities")
public class Identity {

    @Id
    @Column(name = "nic", length = 12)
    private String nic; // Primary key - National Identity Card Number

    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth; // Required for age verification

    @Column(name = "gender", length = 10, nullable = false)
    private String gender; // Required for official documents

    @Column(name = "issued_date", nullable = false)
    private String issuedDate;

    @Column(name = "issued_by", length = 100, nullable = false)
    private String issuedBy;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // Active, Suspended, Revoked

    @Column(name = "wallet_id", length = 100)
    private String walletId;

    @Column(name = "public_key", length = 500)
    private String publicKey;

    @Column(name = "certificate_hash", length = 64)
    private String certificateHash;

    @Column(name = "biometric_hash", length = 64)
    private String biometricHash;

    @Column(name = "personal_data_id")
    private Long personalDataId; // Foreign key to encrypted personal data

    @Column(name = "documents_ipfs_hash", length = 100)
    private String documentsIPFSHash; // IPFS reference for documents

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Transient fields (not stored in DB, populated when needed)
    @Transient
    private String fullName;

    @Transient
    private String address;

    @Transient
    private String phoneNumber;

    @Transient
    private String email;

    @Transient
    private String emergencyContact;

    // Default constructor
    public Identity() {}

    // Getters and Setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getCertificateHash() { return certificateHash; }
    public void setCertificateHash(String certificateHash) { this.certificateHash = certificateHash; }

    public String getBiometricHash() { return biometricHash; }
    public void setBiometricHash(String biometricHash) { this.biometricHash = biometricHash; }

    public Long getPersonalDataId() { return personalDataId; }
    public void setPersonalDataId(Long personalDataId) { this.personalDataId = personalDataId; }

    public String getDocumentsIPFSHash() { return documentsIPFSHash; }
    public void setDocumentsIPFSHash(String documentsIPFSHash) { this.documentsIPFSHash = documentsIPFSHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Transient getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
}
