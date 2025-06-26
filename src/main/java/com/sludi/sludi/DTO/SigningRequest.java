package com.sludi.sludi.DTO;

public class SigningRequest {
    private String walletId;
    private String dataToSign;

    // Getters and setters
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public String getDataToSign() { return dataToSign; }
    public void setDataToSign(String dataToSign) { this.dataToSign = dataToSign; }
}
