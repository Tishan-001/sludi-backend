//package com.sludi.sludi.DTO;
//
//import com.sludi.sludi.domain.Identity;
//import com.sludi.sludi.domain.PersonalData;
//import java.util.List;
//
//public class RegistrationRequest {
//
//    private Identity identity;
//    private PersonalData personalData;
//    private String photoBase64;           // Base64 encoded photo data
//    private String fingerprintBase64;     // Base64 encoded fingerprint data
//    private List<String> documentPaths;   // Document file paths or names
//
//    // Default constructor
//    public RegistrationRequest() {}
//
//    // Constructor with required fields
//    public RegistrationRequest(Identity identity, PersonalData personalData) {
//        this.identity = identity;
//        this.personalData = personalData;
//    }
//
//    // Getters and Setters
//    public Identity getIdentity() {
//        return identity;
//    }
//
//    public void setIdentity(Identity identity) {
//        this.identity = identity;
//    }
//
//    public PersonalData getPersonalData() {
//        return personalData;
//    }
//
//    public void setPersonalData(PersonalData personalData) {
//        this.personalData = personalData;
//    }
//
//    public String getPhotoBase64() {
//        return photoBase64;
//    }
//
//    public void setPhotoBase64(String photoBase64) {
//        this.photoBase64 = photoBase64;
//    }
//
//    public String getFingerprintBase64() {
//        return fingerprintBase64;
//    }
//
//    public void setFingerprintBase64(String fingerprintBase64) {
//        this.fingerprintBase64 = fingerprintBase64;
//    }
//
//    public List<String> getDocumentPaths() {
//        return documentPaths;
//    }
//
//    public void setDocumentPaths(List<String> documentPaths) {
//        this.documentPaths = documentPaths;
//    }
//
//    @Override
//    public String toString() {
//        return "RegistrationRequest{" +
//                "identity=" + (identity != null ? identity.getNic() : "null") +
//                ", personalData=" + (personalData != null ? "present" : "null") +
//                ", photoBase64=" + (photoBase64 != null ? "present" : "null") +
//                ", fingerprintBase64=" + (fingerprintBase64 != null ? "present" : "null") +
//                ", documentPaths=" + documentPaths +
//                '}';
//    }
//}