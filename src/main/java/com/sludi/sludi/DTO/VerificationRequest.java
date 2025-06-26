package com.sludi.sludi.DTO;

public class VerificationRequest {
    private String nic;
    private String challengeData;
    private String signature;

    // Getters and setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getChallengeData() { return challengeData; }
    public void setChallengeData(String challengeData) { this.challengeData = challengeData; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
