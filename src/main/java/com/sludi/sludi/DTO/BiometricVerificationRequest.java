package com.sludi.sludi.DTO;

public class BiometricVerificationRequest {
    private String nic;
    private byte[] fingerprintData;
    private byte[] photoData;
    private String verificationType; // FINGERPRINT, PHOTO, BOTH

    // Constructors
    public BiometricVerificationRequest() {}

    // Getters and Setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public byte[] getFingerprintData() { return fingerprintData; }
    public void setFingerprintData(byte[] fingerprintData) { this.fingerprintData = fingerprintData; }

    public byte[] getPhotoData() { return photoData; }
    public void setPhotoData(byte[] photoData) { this.photoData = photoData; }

    public String getVerificationType() { return verificationType; }
    public void setVerificationType(String verificationType) { this.verificationType = verificationType; }
}