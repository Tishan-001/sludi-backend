package com.sludi.sludi.DTO;

public class VerificationRequest {
    private String nic;
    private String challengeData;
    private String signature;
    private byte[] biometricData; // Optional biometric verification

    // Constructors
    public VerificationRequest() {}

    public VerificationRequest(String nic, String challengeData, String signature) {
        this.nic = nic;
        this.challengeData = challengeData;
        this.signature = signature;
    }

    // Getters and Setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getChallengeData() { return challengeData; }
    public void setChallengeData(String challengeData) { this.challengeData = challengeData; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public byte[] getBiometricData() { return biometricData; }
    public void setBiometricData(byte[] biometricData) { this.biometricData = biometricData; }
}
