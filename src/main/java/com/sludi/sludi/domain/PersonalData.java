package com.sludi.sludi.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_data")
public class PersonalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nic", length = 12, nullable = false, unique = true)
    private String nic;

    @Column(name = "full_name_encrypted", length = 500, nullable = false)
    private String fullName; // AES encrypted

    @Column(name = "address_encrypted", length = 1000)
    private String address; // AES encrypted

    @Column(name = "phone_number_encrypted", length = 200)
    private String phoneNumber; // AES encrypted

    @Column(name = "email_encrypted", length = 500)
    private String email; // AES encrypted

    @Column(name = "emergency_contact_encrypted", length = 1000)
    private String emergencyContact; // AES encrypted

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PersonalData() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PersonalData{" +
                "nic='" + nic + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

