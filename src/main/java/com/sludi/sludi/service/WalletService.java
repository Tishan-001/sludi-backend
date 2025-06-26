package com.sludi.sludi.service;

import com.sludi.sludi.DTO.WalletCreationResult;
import com.sludi.sludi.util.CertificateUtils;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.gateway.Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Service
public class WalletService {

    @Value("${fabric.wallet.path:/tmp/wallet}")
    private String walletPath;

    @Value("${fabric.msp-id}")
    private String mspId;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private Wallet getWallet() throws IOException {
        Path walletDir = Paths.get(walletPath);
        return Wallets.newFileSystemWallet(walletDir);
    }

    /**
     * Creates a new wallet identity for a user
     */
    public WalletCreationResult createUserWallet(String nic, String fullName) throws Exception {
        Wallet wallet = getWallet();

        // Generate unique wallet ID
        String walletId = "wallet_" + nic + "_" + UUID.randomUUID().toString().substring(0, 8);

        // Generate key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Convert public key to string format
        String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // Create a mock certificate for the user ( this should be changed to issued by CA)
        X509Certificate certificate = createMockCertificate(keyPair, nic, fullName);
        String certificateHash = calculateCertificateHash(certificate);

        // Create Fabric identity
        Identity identity = Identities.newX509Identity(mspId, certificate, keyPair.getPrivate());
        // Store in wallet
        wallet.put(walletId, identity);

        return new WalletCreationResult(walletId, publicKeyString, privateKeyString, certificateHash);
    }

    /**
     * Retrieves a user's wallet identity
     */
    public Identity getUserWalletIdentity(String walletId) throws IOException {
        Wallet wallet = getWallet();
        return wallet.get(walletId);
    }

    /**
     * Validates if a wallet exists and is valid
     */
    public boolean validateWallet(String walletId) throws IOException {
        Wallet wallet = getWallet();
        Identity identity = wallet.get(walletId);
        return identity != null;
    }

    /**
     * Generates a digital signature for identity verification
     */
    public String signIdentityData(String walletId, String dataToSign) throws Exception {
        Wallet wallet = getWallet();
        Identity identity = wallet.get(walletId);

        if (identity == null) {
            throw new IllegalArgumentException("Wallet not found: " + walletId);
        }

        if (identity instanceof X509Identity x509Identity) {
            PrivateKey privateKey = x509Identity.getPrivateKey();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(dataToSign.getBytes());

            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        }

        throw new IllegalStateException("Invalid identity type");
    }

    /**
     * Verifies a digital signature
     */
    public boolean verifySignature(String publicKeyString, String dataToVerify, String signatureString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataToVerify.getBytes());

        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }

    /**
     * Creates a mock certificate (for development - use proper CA in production)
     */
    private X509Certificate createMockCertificate(KeyPair keyPair, String nic, String fullName) throws Exception {
        // This is a simplified mock implementation
        // In production, you would use a proper Certificate Authority
        return CertificateUtils.createSelfSignedCertificate(keyPair, nic, fullName);
    }

    /**
     * Calculates hash of certificate for storage
     */
    private String calculateCertificateHash(X509Certificate certificate) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] certBytes = certificate.getEncoded();
        byte[] hashBytes = digest.digest(certBytes);
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * Removes a wallet (for cleanup or revocation)
     */
    public void removeWallet(String walletId) throws IOException {
        Wallet wallet = getWallet();
        wallet.remove(walletId);
    }


}