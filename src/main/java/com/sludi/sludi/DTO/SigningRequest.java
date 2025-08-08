package com.sludi.sludi.DTO;

public class SigningRequest {
    private String walletId;
    private String dataToSign;
    private String purpose; // Optional: reason for signing

    // Constructors
    public SigningRequest() {}

    public SigningRequest(String walletId, String dataToSign) {
        this.walletId = walletId;
        this.dataToSign = dataToSign;
    }

    // Getters and Setters
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public String getDataToSign() { return dataToSign; }
    public void setDataToSign(String dataToSign) { this.dataToSign = dataToSign; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
