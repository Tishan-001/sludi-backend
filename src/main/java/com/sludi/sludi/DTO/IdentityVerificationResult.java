package com.sludi.sludi.DTO;

public record IdentityVerificationResult(
        boolean verified,
        String message,
        String identityData
) {}