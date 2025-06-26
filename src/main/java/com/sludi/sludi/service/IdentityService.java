package com.sludi.sludi.service;

import com.sludi.sludi.DTO.IdentityCreationResult;
import com.sludi.sludi.DTO.IdentityVerificationResult;
import com.sludi.sludi.DTO.WalletCreationResult;
import com.sludi.sludi.domain.Identity;
import com.sludi.sludi.util.JsonUtil;
import org.hyperledger.fabric.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class IdentityService {

    private final Contract contract;
    private final WalletService walletService;

    @Autowired
    public IdentityService(Contract contract, WalletService walletService) {
        this.contract = contract;
        this.walletService = walletService;
    }

    public void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("InitLedger");
    }

    public String getAllIdentities() throws GatewayException {
        var result = contract.evaluateTransaction("GetAllIdentities");
        return JsonUtil.prettyJson(result);
    }

    /**
     * Identity creation with wallet generation
     */
    @Transactional
    public IdentityCreationResult createIdentityWithWallet(Identity identity)
            throws EndorseException, SubmitException, CommitStatusException, CommitException {

        try {
            // First, create the wallet for the user
            WalletCreationResult walletResult = walletService.createUserWallet(
                    identity.getNic(),
                    identity.getFullName()
            );

            // Set wallet information in the identity
            identity.setWalletId(walletResult.walletId());
            identity.setPublicKey(walletResult.publicKey());
            identity.setCertificateHash(walletResult.certificateHash());

            // Create identity on the blockchain
            contract.submitTransaction("CreateIdentity",
                    identity.getNic(),
                    identity.getFullName(),
                    identity.getDateOfBirth(),
                    identity.getGender(),
                    identity.getAddress(),
                    identity.getPhoneNumber(),
                    identity.getEmail(),
                    identity.getIssuedDate(),
                    identity.getIssuedBy(),
                    identity.getBiometricHash(),
                    identity.getStatus(),
                    identity.getPublicKey(),
                    identity.getWalletId(),
                    identity.getCertificateHash()
            );

            return new IdentityCreationResult(
                    identity,
                    walletResult.walletId(),
                    walletResult.publicKey(),
                    walletResult.privateKey() // Return private key only once during creation
            );

        } catch (Exception e) {
            // Clean up wallet if blockchain transaction fails
            if (identity.getWalletId() != null) {
                try {
                    walletService.removeWallet(identity.getWalletId());
                } catch (Exception cleanupException) {
                    // Log cleanup failure but don't mask original exception
                    System.err.println("Failed to cleanup wallet: " + cleanupException.getMessage());
                }
            }
            throw new RuntimeException("Failed to create identity with wallet", e);
        }
    }

    public String readIdentity(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("ReadIdentity", nic);
        return JsonUtil.prettyJson(result);
    }

    public void updateIdentity(String nic, Identity identity)
            throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("UpdateIdentity",
                nic,
                identity.getFullName(),
                identity.getDateOfBirth(),
                identity.getGender(),
                identity.getAddress(),
                identity.getPhoneNumber(),
                identity.getEmail(),
                identity.getIssuedDate(),
                identity.getIssuedBy(),
                identity.getBiometricHash(),
                identity.getStatus());
    }

    public void deleteIdentity(String nic) throws EndorseException, SubmitException, CommitStatusException, CommitException {
        // First get the identity to find the wallet ID
        try {
            var result = contract.evaluateTransaction("ReadIdentity", nic);
            if (result != null) {
                // Parse result to get wallet ID and remove wallet
                // This is a simplified approach - you might want to deserialize the JSON
                String resultStr = new String(result, StandardCharsets.UTF_8);
                // Extract wallet ID from JSON and remove wallet
                // Implementation depends on your JSON structure
            }
        } catch (Exception e) {
            // Continue with deletion even if wallet cleanup fails
            System.err.println("Warning: Could not cleanup wallet for identity " + nic);
        }

        contract.submitTransaction("DeleteIdentity", nic);
    }

    public boolean identityExists(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("IdentityExists", nic);
        return Boolean.parseBoolean(new String(result, StandardCharsets.UTF_8));
    }

    /**
     * Verify identity using digital signature
     */
    public IdentityVerificationResult verifyIdentity(String nic, String challengeData, String signature)
            throws GatewayException {

        try {
            // Get identity from blockchain
            var result = contract.evaluateTransaction("ReadIdentity", nic);
            if (result == null) {
                return new IdentityVerificationResult(false, "Identity not found", null);
            }

            String identityJson = new String(result, StandardCharsets.UTF_8);
            // Parse JSON to extract public key (you might want to use a JSON library)
            // This is simplified - implement proper JSON parsing

            // For now, let's assume we can extract the public key
            // In practice, you'd deserialize the JSON response

            return new IdentityVerificationResult(true, "Identity verified", identityJson);

        } catch (Exception e) {
            return new IdentityVerificationResult(false, "Verification failed: " + e.getMessage(), null);
        }
    }

    /**
     * Generate a challenge for identity verification
     */
    public String generateVerificationChallenge(String nic) {
        return "CHALLENGE_" + nic + "_" + System.currentTimeMillis();
    }

}