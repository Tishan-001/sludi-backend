package com.sludi.sludi.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CertificateUtils {

    /**
     * Creates a self-signed certificate for development purposes
     * In production, this should be replaced with proper CA-issued certificates
     */
    public static X509Certificate createSelfSignedCertificate(KeyPair keyPair, String nic, String fullName)
            throws OperatorCreationException, CertificateException {

        // Certificate validity period
        LocalDateTime notBefore = LocalDateTime.now();
        LocalDateTime notAfter = notBefore.plusYears(1);

        // Subject and issuer (same for self-signed)
        X500Name subject = new X500Name("CN=" + fullName + ", OU=Citizens, O=SLUDI, C=LK");
        X500Name issuer = subject; // Self-signed

        // Serial number
        BigInteger serialNumber = new BigInteger(64, new java.security.SecureRandom());

        // Build certificate
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                Date.from(notBefore.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(notAfter.atZone(ZoneId.systemDefault()).toInstant()),
                subject,
                keyPair.getPublic()
        );

        // Sign the certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = certBuilder.build(signer);

        // Convert to X509Certificate
        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }
}