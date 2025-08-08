package com.sludi.sludi.service;

import com.sludi.sludi.DTO.IdentityCreationResult;
import com.sludi.sludi.DTO.IdentityVerificationResult;
import com.sludi.sludi.DTO.WalletCreationResult;
import com.sludi.sludi.domain.Identity;
import com.sludi.sludi.domain.PersonalData;
import com.sludi.sludi.domain.IdentityDocument;
import com.sludi.sludi.repository.IdentityRepository;
import com.sludi.sludi.repository.PersonalDataRepository;
import com.sludi.sludi.repository.IdentityDocumentRepository;
import com.sludi.sludi.util.JsonUtil;
import com.sludi.sludi.util.EncryptionUtil;
import org.hyperledger.fabric.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class IdentityService {

    private final Contract contract;
    private final WalletService walletService;
    private final IpfsService ipfsService;
    private final IdentityRepository identityRepository;
    private final PersonalDataRepository personalDataRepository;
    private final IdentityDocumentRepository documentRepository;
    private final EncryptionUtil encryptionUtil;

    @Autowired
    public IdentityService(Contract contract, WalletService walletService, IpfsService ipfsService,
                           IdentityRepository identityRepository, PersonalDataRepository personalDataRepository,
                           IdentityDocumentRepository documentRepository, EncryptionUtil encryptionUtil) {
        this.contract = contract;
        this.walletService = walletService;
        this.ipfsService = ipfsService;
        this.identityRepository = identityRepository;
        this.personalDataRepository = personalDataRepository;
        this.documentRepository = documentRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("InitLedger");
    }

    public String getAllIdentities() throws GatewayException {
        var result = contract.evaluateTransaction("GetAllIdentities");
        return JsonUtil.prettyJson(result);
    }

    /**
     * Complete identity creation with proper data separation
     */
    @Transactional
    public IdentityCreationResult createIdentityWithWallet(Identity identity, byte[] photoData,
                                                           byte[] fingerprintData, String[] supportingDocuments)
            throws Exception {

        try {
            // 1. Create wallet for digital signatures
            WalletCreationResult walletResult = walletService.createUserWallet(
                    identity.getNic(),
                    identity.getFullName()
            );

            // 2. Process and encrypt biometric data for IPFS storage
            String biometricHash = processBiometricData(photoData, fingerprintData);

            // 3. Store personal data in encrypted PostgreSQL
            PersonalData personalData = new PersonalData();
            personalData.setNic(identity.getNic());
            personalData.setFullName(encryptionUtil.encrypt(identity.getFullName()));
            personalData.setAddress(encryptionUtil.encrypt(identity.getAddress()));
            personalData.setPhoneNumber(encryptionUtil.encrypt(identity.getPhoneNumber()));
            personalData.setEmail(encryptionUtil.encrypt(identity.getEmail()));
            personalData.setEmergencyContact(encryptionUtil.encrypt(identity.getEmergencyContact()));
            personalData.setCreatedAt(java.time.LocalDateTime.now());

            PersonalData savedPersonalData = personalDataRepository.save(personalData);
            String personalDataHash = generateHash(personalData.toString());

            // 4. Store supporting documents in IPFS
            String documentsIPFSHash = storeDocumentsInIPFS(identity.getNic(), supportingDocuments);

            // 5. Store core identity in PostgreSQL (non-encrypted basic info)
            Identity coreIdentity = new Identity();
            coreIdentity.setNic(identity.getNic());
            coreIdentity.setDateOfBirth(identity.getDateOfBirth());
            coreIdentity.setGender(identity.getGender());
            coreIdentity.setIssuedDate(identity.getIssuedDate());
            coreIdentity.setIssuedBy(identity.getIssuedBy());
            coreIdentity.setStatus("Active");
            coreIdentity.setWalletId(walletResult.walletId());
            coreIdentity.setPublicKey(walletResult.publicKey());
            coreIdentity.setCertificateHash(walletResult.certificateHash());
            coreIdentity.setBiometricHash(biometricHash);
            coreIdentity.setPersonalDataId(savedPersonalData.getId());
            coreIdentity.setDocumentsIPFSHash(documentsIPFSHash);
            coreIdentity.setCreatedAt(java.time.LocalDateTime.now());

            Identity savedIdentity = identityRepository.save(coreIdentity);

            // 6. Create blockchain record with minimal data
            String fullNameHash = generateHash(identity.getFullName());

            contract.submitTransaction("CreateIdentity",
                    identity.getNic(),
                    fullNameHash,
                    identity.getDateOfBirth(),
                    identity.getGender(),
                    biometricHash,
                    identity.getIssuedDate(),
                    identity.getIssuedBy(),
                    "Active",
                    walletResult.publicKey(),
                    walletResult.certificateHash(),
                    documentsIPFSHash,
                    personalDataHash
            );

            return new IdentityCreationResult(
                    savedIdentity,
                    walletResult.walletId(),
                    walletResult.publicKey(),
                    walletResult.privateKey() // Return only once during creation
            );

        } catch (Exception e) {
            // Cleanup on failure
            if (identity.getWalletId() != null) {
                try {
                    walletService.removeWallet(identity.getWalletId());
                } catch (Exception cleanupException) {
                    System.err.println("Failed to cleanup wallet: " + cleanupException.getMessage());
                }
            }
            throw new RuntimeException("Failed to create identity with wallet", e);
        }
    }

    /**
     * Read complete identity (combining blockchain + DB + IPFS data)
     */
    public Identity getCompleteIdentity(String nic, boolean includePersonalData) throws Exception {
        // Get blockchain data
        var blockchainResult = contract.evaluateTransaction("ReadIdentity", nic);
        String blockchainData = new String(blockchainResult, StandardCharsets.UTF_8);

        // Get core identity from DB
        Identity coreIdentity = identityRepository.findByNic(nic)
                .orElseThrow(() -> new RuntimeException("Identity not found in database"));

        if (includePersonalData) {
            // Get personal data and decrypt
            PersonalData personalData = personalDataRepository.findById(coreIdentity.getPersonalDataId())
                    .orElseThrow(() -> new RuntimeException("Personal data not found"));

            // Decrypt personal data
            coreIdentity.setFullName(encryptionUtil.decrypt(personalData.getFullName()));
            coreIdentity.setAddress(encryptionUtil.decrypt(personalData.getAddress()));
            coreIdentity.setPhoneNumber(encryptionUtil.decrypt(personalData.getPhoneNumber()));
            coreIdentity.setEmail(encryptionUtil.decrypt(personalData.getEmail()));
        }

        return coreIdentity;
    }

    /**
     * Update identity status on blockchain
     */
    public void updateIdentityStatus(String nic, String newStatus, String reason)
            throws EndorseException, SubmitException, CommitStatusException, CommitException {

        // Update in database
        Identity identity = identityRepository.findByNic(nic)
                .orElseThrow(() -> new RuntimeException("Identity not found"));

        String oldStatus = identity.getStatus();
        identity.setStatus(newStatus);
        identity.setUpdatedAt(java.time.LocalDateTime.now());
        identityRepository.save(identity);

        // Update on blockchain
        contract.submitTransaction("UpdateIdentityStatus", nic, newStatus, reason);

        // Log the change
        System.out.printf("Identity %s status changed from %s to %s. Reason: %s%n",
                nic, oldStatus, newStatus, reason);
    }

    /**
     * Update personal data (off-chain only)
     */
    @Transactional
    public void updatePersonalData(String nic, PersonalData newPersonalData) throws Exception {
        Identity identity = identityRepository.findByNic(nic)
                .orElseThrow(() -> new RuntimeException("Identity not found"));

        // Get existing personal data
        PersonalData existingData = personalDataRepository.findById(identity.getPersonalDataId())
                .orElseThrow(() -> new RuntimeException("Personal data not found"));

        // Update and encrypt new data
        if (newPersonalData.getAddress() != null) {
            existingData.setAddress(encryptionUtil.encrypt(newPersonalData.getAddress()));
        }
        if (newPersonalData.getPhoneNumber() != null) {
            existingData.setPhoneNumber(encryptionUtil.encrypt(newPersonalData.getPhoneNumber()));
        }
        if (newPersonalData.getEmail() != null) {
            existingData.setEmail(encryptionUtil.encrypt(newPersonalData.getEmail()));
        }

        existingData.setUpdatedAt(java.time.LocalDateTime.now());
        PersonalData updatedData = personalDataRepository.save(existingData);

        // Update hash on blockchain
        String newPersonalDataHash = generateHash(updatedData.toString());
        contract.submitTransaction("UpdateIdentityHashes", nic,
                identity.getDocumentsIPFSHash(), newPersonalDataHash);
    }

    /**
     * Verify identity using multiple factors
     */
    public IdentityVerificationResult verifyIdentity(String nic, String challengeData,
                                                     String signature, byte[] biometricData) throws Exception {

        try {
            // Get blockchain identity
            var result = contract.evaluateTransaction("ReadIdentity", nic);
            if (result == null) {
                return new IdentityVerificationResult(false, "Identity not found", null);
            }

            // Get database identity for additional verification
            Identity dbIdentity = identityRepository.findByNic(nic)
                    .orElse(null);

            if (dbIdentity == null || !"Active".equals(dbIdentity.getStatus())) {
                return new IdentityVerificationResult(false, "Identity not active", null);
            }

            // Verify digital signature using public key
            boolean signatureValid = walletService.verifySignature(
                    challengeData, signature, dbIdentity.getPublicKey());

            // Verify biometric data if provided
            boolean biometricValid = true;
            if (biometricData != null) {
                String providedBiometricHash = generateHash(biometricData);
                biometricValid = providedBiometricHash.equals(dbIdentity.getBiometricHash());
            }

            boolean isVerified = signatureValid && biometricValid;

            if (isVerified) {
                // Record successful verification on blockchain
                contract.submitTransaction("VerifyIdentity", nic, "SYSTEM", "DIGITAL_SIGNATURE");
            }

            String identityJson = new String(result, StandardCharsets.UTF_8);
            return new IdentityVerificationResult(isVerified,
                    isVerified ? "Identity verified successfully" : "Verification failed",
                    identityJson);

        } catch (Exception e) {
            return new IdentityVerificationResult(false, "Verification error: " + e.getMessage(), null);
        }
    }

    /**
     * Get identity modification history from blockchain
     */
    public String getIdentityHistory(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("GetIdentityHistory", nic);
        return JsonUtil.prettyJson(result);
    }

    // Helper methods
    private String processBiometricData(byte[] photoData, byte[] fingerprintData) throws Exception {
        // Process and store biometric data securely in IPFS
        String photoHash = ipfsService.uploadFile(photoData, "photo_" + System.currentTimeMillis() + ".jpg");
        String fingerprintHash = ipfsService.uploadFile(fingerprintData, "fp_" + System.currentTimeMillis() + ".dat");

        // Return combined hash
        return generateHash(photoHash + fingerprintHash);
    }

    private String storeDocumentsInIPFS(String nic, String[] documents) throws Exception {
        if (documents == null || documents.length == 0) {
            return "";
        }

        StringBuilder documentsData = new StringBuilder();
        for (String doc : documents) {
            // In practice, these would be actual document files
            String docHash = ipfsService.uploadFile(doc.getBytes(), "doc_" + nic + "_" + System.currentTimeMillis());
            documentsData.append(docHash).append(",");
        }

        // Store the combined document reference
        String combinedDocs = documentsData.toString();
        return ipfsService.uploadFile(combinedDocs.getBytes(), "docs_" + nic + ".json");
    }

    private String generateHash(Object data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String generateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public boolean identityExists(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("IdentityExists", nic);
        return Boolean.parseBoolean(new String(result, StandardCharsets.UTF_8));
    }

    public String generateVerificationChallenge(String nic) {
        return "CHALLENGE_" + nic + "_" + System.currentTimeMillis();
    }

    /**
     * Read identity from blockchain (public method for verification)
     */
    public String readIdentity(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("ReadIdentity", nic);
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * Save document metadata to database
     */
    @Transactional
    public IdentityDocument saveDocument(IdentityDocument document) {
        document.setUploadDate(java.time.LocalDateTime.now());
        return documentRepository.save(document);
    }

    /**
     * Get documents by NIC
     */
    public List<IdentityDocument> getDocumentsByNic(String nic) {
        return documentRepository.findByNic(nic);
    }

    /**
     * Get documents by NIC with pagination
     */
    public List<IdentityDocument> getDocumentsByNic(String nic, int page, int size) {
        // If using Spring Data JPA Pageable
        // Pageable pageable = PageRequest.of(page, size);
        // Page<IdentityDocument> documents = documentRepository.findByNic(nic, pageable);
        // return documents.getContent();

        // For now, return all documents
        return documentRepository.findByNic(nic);
    }
}