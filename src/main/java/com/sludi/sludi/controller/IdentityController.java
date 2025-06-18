package com.sludi.sludi.controller;

import com.sludi.sludi.domain.Identity;
import com.sludi.sludi.service.IdentityService;
import com.sludi.sludi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/identity")
@CrossOrigin(origins = "*") // Configure appropriately for production
public class IdentityController {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/init")
    public ResponseEntity<String> initLedger() throws Exception {
        identityService.initLedger();
        return ResponseEntity.ok("Ledger initialized successfully");
    }

    @GetMapping("/identities")
    public ResponseEntity<String> getAllIdentities() throws Exception {
        return ResponseEntity.ok(identityService.getAllIdentities());
    }

    /**
     * Government endpoint - Create identity with wallet
     */
    @PostMapping("/identities/register")
    public ResponseEntity<Map<String, Object>> registerIdentityWithWallet(@RequestBody Identity request) throws Exception {
        IdentityService.IdentityCreationResult result = identityService.createIdentityWithWallet(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Identity registered successfully with wallet");
        response.put("identity", result.getIdentity());
        response.put("walletId", result.getWalletId());
        response.put("publicKey", result.getPublicKey());
        // Private key is included only in the registration response for secure storage by the user
        response.put("privateKey", result.getPrivateKey());
        response.put("warning", "Store the private key securely. It will not be retrievable later.");

        return ResponseEntity.ok(response);
    }

    /**
     * Backward compatibility - Traditional identity creation
     */
    @PostMapping("/identities")
    public ResponseEntity<String> createIdentity(@RequestBody Identity request) throws Exception {
        identityService.createIdentity(request);
        return ResponseEntity.ok("Identity created successfully");
    }

    @GetMapping("/identities/{nic}")
    public ResponseEntity<String> readIdentity(@PathVariable String nic) throws Exception {
        return ResponseEntity.ok(identityService.readIdentity(nic));
    }

    @PutMapping("/identities/{nic}")
    public ResponseEntity<String> updateIdentity(@PathVariable String nic, @RequestBody Identity request)
            throws Exception {
        identityService.updateIdentity(nic, request);
        return ResponseEntity.ok("Identity updated successfully");
    }

    @DeleteMapping("/identities/{nic}")
    public ResponseEntity<String> deleteIdentity(@PathVariable String nic) throws Exception {
        identityService.deleteIdentity(nic);
        return ResponseEntity.ok("Identity deleted successfully");
    }

    @GetMapping("/identities/{nic}/exists")
    public ResponseEntity<Boolean> identityExists(@PathVariable String nic) throws Exception {
        return ResponseEntity.ok(identityService.identityExists(nic));
    }

    /**
     * Third-party endpoint - Generate verification challenge
     */
    @PostMapping("/verify/challenge/{nic}")
    public ResponseEntity<Map<String, String>> generateChallenge(@PathVariable String nic) throws Exception {
        if (!identityService.identityExists(nic)) {
            return ResponseEntity.notFound().build();
        }

        String challenge = identityService.generateVerificationChallenge(nic);
        Map<String, String> response = new HashMap<>();
        response.put("challenge", challenge);
        response.put("nic", nic);
        response.put("message", "Sign this challenge with your private key for verification");

        return ResponseEntity.ok(response);
    }

    /**
     * Third-party endpoint - Verify identity using signed challenge
     */
    @PostMapping("/verify/identity")
    public ResponseEntity<Map<String, Object>> verifyIdentity(@RequestBody VerificationRequest request) throws Exception {
        IdentityService.IdentityVerificationResult result = identityService.verifyIdentity(
                request.getNic(),
                request.getChallengeData(),
                request.getSignature()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("verified", result.isVerified());
        response.put("message", result.getMessage());

        if (result.isVerified()) {
            response.put("identityData", result.getIdentityData());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * User endpoint - Sign data with wallet
     */
    @PostMapping("/wallet/sign")
    public ResponseEntity<Map<String, String>> signData(@RequestBody SigningRequest request) throws Exception {
        try {
            String signature = walletService.signIdentityData(request.getWalletId(), request.getDataToSign());

            Map<String, String> response = new HashMap<>();
            response.put("signature", signature);
            response.put("message", "Data signed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Signing failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Utility endpoint - Verify signature
     */
    @PostMapping("/wallet/verify-signature")
    public ResponseEntity<Map<String, Boolean>> verifySignature(@RequestBody SignatureVerificationRequest request) throws Exception {
        boolean isValid = walletService.verifySignature(
                request.getPublicKey(),
                request.getDataToVerify(),
                request.getSignature()
        );

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);

        return ResponseEntity.ok(response);
    }

    /**
     * Wallet validation endpoint
     */
    @GetMapping("/wallet/{walletId}/validate")
    public ResponseEntity<Map<String, Boolean>> validateWallet(@PathVariable String walletId) throws Exception {
        boolean isValid = walletService.validateWallet(walletId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);

        return ResponseEntity.ok(response);
    }

    // Request DTOs
    public static class VerificationRequest {
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

    public static class SigningRequest {
        private String walletId;
        private String dataToSign;

        // Getters and setters
        public String getWalletId() { return walletId; }
        public void setWalletId(String walletId) { this.walletId = walletId; }

        public String getDataToSign() { return dataToSign; }
        public void setDataToSign(String dataToSign) { this.dataToSign = dataToSign; }
    }

    public static class SignatureVerificationRequest {
        private String publicKey;
        private String dataToVerify;
        private String signature;

        // Getters and setters
        public String getPublicKey() { return publicKey; }
        public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

        public String getDataToVerify() { return dataToVerify; }
        public void setDataToVerify(String dataToVerify) { this.dataToVerify = dataToVerify; }

        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
    }
}