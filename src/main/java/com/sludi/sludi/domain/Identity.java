package com.sludi.sludi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "identities", uniqueConstraints = @UniqueConstraint(columnNames = "nic"))
public class Identity {
    @Id
    @Size(max = 50, message = "NIC must not exceed 50 characters")
    @Column(length = 50)
    private String nic;

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    @Column(length = 100)
    private String fullName;

    @Size(max = 10, message = "Date of birth must not exceed 10 characters")
    @Column(length = 10)
    private String dateOfBirth;

    @Size(max = 10, message = "Gender must not exceed 10 characters")
    @Column(length = 10)
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(length = 20)
    private String phoneNumber;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(length = 100)
    private String email;

    @Size(max = 10, message = "Issued date must not exceed 10 characters")
    @Column(length = 10) // e.g., "YYYY-MM-DD"
    private String issuedDate;

    @Size(max = 100, message = "Issued by must not exceed 100 characters")
    @Column(length = 100)
    private String issuedBy;

    @Column(columnDefinition = "TEXT") // Allow long biometric hashes
    private String biometricHash;

    @Size(max = 20, message = "Status must not exceed 20 characters")
    @Column(length = 20)
    private String status;

    @JsonProperty("publicKey")
    @Column(columnDefinition = "TEXT")
    private String publicKey;

    @JsonProperty("walletId")
    @Size(max = 100, message = "Wallet ID must not exceed 100 characters")
    @Column(length = 100)
    private String walletId;

    @JsonProperty("certificateHash")
    @Column(columnDefinition = "TEXT")
    private String certificateHash;

    // Constructors
    public Identity() {}

    public Identity(String nic, String fullName, String dateOfBirth, String gender,
                    String address, String phoneNumber, String email, String issuedDate,
                    String issuedBy, String biometricHash, String status) {
        this.nic = nic;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.issuedDate = issuedDate;
        this.issuedBy = issuedBy;
        this.biometricHash = biometricHash;
        this.status = status;
    }

    // Existing getters and setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public String getBiometricHash() { return biometricHash; }
    public void setBiometricHash(String biometricHash) { this.biometricHash = biometricHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // New wallet-related getters and setters
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public String getCertificateHash() { return certificateHash; }
    public void setCertificateHash(String certificateHash) { this.certificateHash = certificateHash; }
}