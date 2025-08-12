package com.sludi.sludi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sludi.sludi.DTO.*;
import com.sludi.sludi.domain.Identity;
import com.sludi.sludi.domain.PersonalData;
import com.sludi.sludi.domain.IdentityDocument;
import com.sludi.sludi.service.IdentityService;
import com.sludi.sludi.service.WalletService;
import com.sludi.sludi.service.IpfsService;
import com.sludi.sludi.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/identity")
@CrossOrigin(origins = "*") // Configure appropriately for production
public class IdentityController {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private IpfsService ipfsService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    // ===============================
    // GOVERNMENT ENDPOINTS (Admin Only)
    // ===============================

    @PostMapping("/admin/init")
    public ResponseEntity<Map<String, Object>> initLedger(HttpServletRequest request) {
        try {
            identityService.initLedger();

            auditService.logAction("SYSTEM", "INIT_LEDGER", "SYSTEM",
                    "Blockchain ledger initialized", request.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Blockchain ledger initialized successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Failed to initialize ledger", e);
        }
    }

    @GetMapping("/admin/identities")
    public ResponseEntity<Map<String, Object>> getAllIdentities(
            @RequestParam(defaultValue = "false") boolean includePersonalData,
            HttpServletRequest request) {
        try {
            String identitiesJson = identityService.getAllIdentities();

            auditService.logAction("SYSTEM", "LIST_ALL_IDENTITIES", "ADMIN",
                    "Retrieved all identities list", request.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", identitiesJson);
            response.put("includePersonalData", includePersonalData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve identities", e);
        }
    }

    /**
     * Complete identity registration with documents and biometric data
     */
    @PostMapping("/admin/register")
    public ResponseEntity<Map<String, Object>> registerCompleteIdentity(
            @RequestPart("identity") String identityJson,
            @RequestPart("personalData") String personalDataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "fingerprint", required = false) MultipartFile fingerprint,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents,
            HttpServletRequest request) {

        try {
            //Deserialize JSON String to POJOs
            Identity identity = objectMapper.readValue(identityJson, Identity.class);
            PersonalData personalData = objectMapper.readValue(personalDataJson, PersonalData.class);
            // Validate required fields
            if (identity.getNic() == null || identity.getNic().trim().isEmpty()) {
                return createErrorResponse("NIC is required", null);
            }

            // Process biometric data
            byte[] photoData = photo != null ? photo.getBytes() : null;
            byte[] fingerprintData = fingerprint != null ? fingerprint.getBytes() : null;

            // Process supporting documents
            String[] documentPaths = null;
            if (documents != null && documents.length > 0) {
                documentPaths = new String[documents.length];
                for (int i = 0; i < documents.length; i++) {
                    documentPaths[i] = documents[i].getOriginalFilename();
                }
            }

            IdentityCreationResult result = identityService.createIdentityWithWallet(
                    identity, photoData, fingerprintData, documentPaths);

            // Log successful registration
            auditService.logAction(identity.getNic(), "IDENTITY_REGISTERED", "ADMIN",
                    "Complete identity registration successful", request.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Identity registered successfully with wallet and documents");
            response.put("identity", maskSensitiveData(result.identity()));
            response.put("walletId", result.walletId());
            response.put("publicKey", result.publicKey());
            response.put("privateKey", result.privateKey()); // Only returned once
            response.put("warning", "CRITICAL: Store the private key securely. It cannot be recovered if lost.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            auditService.logAction( "UNKNOWN",
                    "IDENTITY_REGISTRATION_FAILED", "ADMIN",
                    "Registration failed: " + e.getMessage(), request.getRemoteAddr());
            return createErrorResponse("Identity registration failed", e);
        }
    }

    @PutMapping("/admin/identities/{nic}/status")
    public ResponseEntity<Map<String, Object>> updateIdentityStatus(
            @PathVariable String nic,
            @RequestBody Map<String, String> statusUpdate,
            HttpServletRequest request) {

        try {
            String newStatus = statusUpdate.get("status");
            String reason = statusUpdate.get("reason");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                return createErrorResponse("Status is required", null);
            }

            identityService.updateIdentityStatus(nic, newStatus, reason);

            auditService.logAction(nic, "STATUS_UPDATED", "ADMIN",
                    String.format("Status changed to %s. Reason: %s", newStatus, reason),
                    request.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Identity status updated successfully");
            response.put("nic", nic);
            response.put("newStatus", newStatus);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Failed to update identity status", e);
        }
    }

    // ===============================
    // PUBLIC VERIFICATION ENDPOINTS
    // ===============================

    @GetMapping("/public/{nic}/verify")
    public ResponseEntity<Map<String, Object>> getPublicIdentityInfo(@PathVariable String nic) {
        try {
            // Only return non-sensitive blockchain data for verification
            String blockchainData = identityService.readIdentity(nic);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", true);
            response.put("blockchainData", blockchainData);
            response.put("message", "Identity verification data retrieved");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("exists", false);
            response.put("message", "Identity not found or verification failed");

            return ResponseEntity.ok(response); // Don't expose internal errors
        }
    }

    @PostMapping("/public/verify/challenge/{nic}")
    public ResponseEntity<Map<String, Object>> generateVerificationChallenge(@PathVariable String nic) {
        try {
            if (!identityService.identityExists(nic)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Identity not found");
                return ResponseEntity.ok(response);
            }

            String challenge = identityService.generateVerificationChallenge(nic);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("challenge", challenge);
            response.put("nic", nic);
            response.put("expiresIn", 300); // 5 minutes
            response.put("instructions", "Sign this challenge with your private key for identity verification");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Failed to generate verification challenge", e);
        }
    }

    @PostMapping("/public/verify/identity")
    public ResponseEntity<Map<String, Object>> verifyIdentityWithChallenge(
            @RequestBody VerificationRequest request,
            HttpServletRequest httpRequest) {

        try {
            IdentityVerificationResult result = identityService.verifyIdentity(
                    request.getNic(),
                    request.getChallengeData(),
                    request.getSignature(),
                    request.getBiometricData() // Optional biometric verification
            );

            // Log verification attempt
            auditService.logAction(request.getNic(), "IDENTITY_VERIFICATION", "PUBLIC",
                    "Verification result: " + result.verified(), httpRequest.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("verified", result.verified());
            response.put("message", result.message());
            response.put("timestamp", java.time.LocalDateTime.now());

            if (result.verified()) {
                // Return only necessary verification data, not full identity
                Map<String, Object> verificationData = new HashMap<>();
                verificationData.put("nic", request.getNic());
                verificationData.put("status", "Active");
                verificationData.put("verifiedAt", java.time.LocalDateTime.now());
                response.put("verificationData", verificationData);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Identity verification failed", e);
        }
    }

    // ===============================
    // USER ENDPOINTS (Wallet Operations)
    // ===============================

    @PostMapping("/user/wallet/sign")
    public ResponseEntity<Map<String, Object>> signDataWithWallet(
            @RequestBody SigningRequest request,
            HttpServletRequest httpRequest) {

        try {
            String signature = walletService.signIdentityData(request.getWalletId(), request.getDataToSign());

            auditService.logAction(request.getWalletId(), "DATA_SIGNED", "USER",
                    "Data signed with wallet", httpRequest.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("signature", signature);
            response.put("message", "Data signed successfully");
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Signing operation failed", e);
        }
    }

    @GetMapping("/user/wallet/{walletId}/validate")
    public ResponseEntity<Map<String, Object>> validateUserWallet(@PathVariable String walletId) {
        try {
            boolean isValid = walletService.validateWallet(walletId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", isValid);
            response.put("walletId", walletId);
            response.put("message", isValid ? "Wallet is valid" : "Wallet is invalid");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Wallet validation failed", e);
        }
    }

    // ===============================
    // DOCUMENT MANAGEMENT ENDPOINTS
    // ===============================

    @PostMapping("/documents/{nic}/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @PathVariable String nic,
            @RequestParam("documentType") String documentType,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request) {

        try {
            if (file.isEmpty()) {
                return createErrorResponse("File is required", null);
            }

            // Upload to IPFS
            String ipfsHash = ipfsService.uploadFile(file.getBytes(), file.getOriginalFilename());

            // Create document record
            IdentityDocument document = new IdentityDocument();
            document.setNic(nic);
            document.setDocumentType(documentType);
            document.setIpfsHash(ipfsHash);
            document.setFileName(file.getOriginalFilename());
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());
            document.setUploadDate(java.time.LocalDateTime.now());

            // Save document metadata
            // identityService.saveDocument(document); // Implement this method

            auditService.logAction(nic, "DOCUMENT_UPLOADED", "USER",
                    String.format("Document uploaded: %s (%s)", documentType, file.getOriginalFilename()),
                    request.getRemoteAddr());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document uploaded successfully");
            response.put("documentId", document.getId());
            response.put("ipfsHash", ipfsHash);
            response.put("documentType", documentType);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Document upload failed", e);
        }
    }

    @GetMapping("/documents/{nic}")
    public ResponseEntity<Map<String, Object>> getDocumentsList(@PathVariable String nic) {
        try {
            // List<IdentityDocument> documents = identityService.getDocumentsByNic(nic);
            // Implement this method in service

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("nic", nic);
            // response.put("documents", documents);
            response.put("message", "Documents retrieved successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve documents", e);
        }
    }

    // ===============================
    // AUDIT AND HISTORY ENDPOINTS
    // ===============================

    @GetMapping("/audit/{nic}/history")
    public ResponseEntity<Map<String, Object>> getIdentityHistory(@PathVariable String nic) {
        try {
            // Get blockchain history
            String blockchainHistory = identityService.getIdentityHistory(nic);

            // Get database audit log
            // List<IdentityAuditLog> auditLog = auditService.getAuditLogByNic(nic);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("nic", nic);
            response.put("blockchainHistory", blockchainHistory);
            // response.put("auditLog", auditLog);
            response.put("message", "Identity history retrieved successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Failed to retrieve identity history", e);
        }
    }

    // ===============================
    // UTILITY METHODS
    // ===============================

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);

        if (e != null) {
            response.put("error", e.getMessage());
            // Log error details (don't expose to client in production)
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Identity maskSensitiveData(Identity identity) {
        // Create a copy with sensitive data masked for response
        Identity maskedIdentity = new Identity();
        maskedIdentity.setNic(identity.getNic());
        maskedIdentity.setDateOfBirth(identity.getDateOfBirth());
        maskedIdentity.setGender(identity.getGender());
        maskedIdentity.setStatus(identity.getStatus());
        maskedIdentity.setIssuedDate(identity.getIssuedDate());
        maskedIdentity.setIssuedBy(identity.getIssuedBy());
        maskedIdentity.setWalletId(identity.getWalletId());
        maskedIdentity.setPublicKey(identity.getPublicKey());
        maskedIdentity.setCreatedAt(identity.getCreatedAt());

        // Mask personal data
        if (identity.getFullName() != null) {
            maskedIdentity.setFullName(maskString(identity.getFullName()));
        }
        if (identity.getAddress() != null) {
            maskedIdentity.setAddress(maskString(identity.getAddress()));
        }
        if (identity.getPhoneNumber() != null) {
            maskedIdentity.setPhoneNumber(maskPhoneNumber(identity.getPhoneNumber()));
        }
        if (identity.getEmail() != null) {
            maskedIdentity.setEmail(maskEmail(identity.getEmail()));
        }

        return maskedIdentity;
    }

    private String maskString(String input) {
        if (input == null || input.length() < 3) return input;
        return input.substring(0, 2) + "*".repeat(input.length() - 2);
    }

    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) return phone;
        return phone.substring(0, 3) + "*".repeat(phone.length() - 6) + phone.substring(phone.length() - 3);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        if (parts[0].length() < 3) return email;
        return parts[0].substring(0, 2) + "*".repeat(parts[0].length() - 2) + "@" + parts[1];
    }

    // ===============================
    // HEALTH CHECK ENDPOINT
    // ===============================

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "National Digital Identity System");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("version", "2.0.0");

        return ResponseEntity.ok(response);
    }
}