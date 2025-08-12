package com.sludi.sludi.DTO;

public class PersonalDataUpdateRequest {
    private String address;
    private String phoneNumber;
    private String email;
    private String emergencyContact;
    private String reason; // Reason for update

    // Constructors
    public PersonalDataUpdateRequest() {}

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}