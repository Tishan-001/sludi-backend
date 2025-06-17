package com.sludi.sludi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Identity {
    private String nic;
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String address;
    private String phoneNumber;
    private String email;
    private String issuedDate;
    private String issuedBy;
    private String biometricHash;
    private String status;

    // New wallet-related fields
    @JsonProperty("publicKey")
    private String publicKey;

    @JsonProperty("walletId")
    private String walletId;

    @JsonProperty("certificateHash")
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