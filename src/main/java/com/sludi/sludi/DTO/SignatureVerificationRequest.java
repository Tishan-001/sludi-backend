package com.sludi.sludi.DTO;

public class SignatureVerificationRequest {
    private String publicKey;
    private String dataToVerify;
    private String signature;

    // Constructors
    public SignatureVerificationRequest() {}

    public SignatureVerificationRequest(String publicKey, String dataToVerify, String signature) {
        this.publicKey = publicKey;
        this.dataToVerify = dataToVerify;
        this.signature = signature;
    }

    // Getters and Setters
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getDataToVerify() { return dataToVerify; }
    public void setDataToVerify(String dataToVerify) { this.dataToVerify = dataToVerify; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
