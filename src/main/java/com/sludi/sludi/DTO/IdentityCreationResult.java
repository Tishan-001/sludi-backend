package com.sludi.sludi.DTO;

import com.sludi.sludi.domain.Identity;

/**
 * @param privateKey Only provided during creation
 */
public record IdentityCreationResult(
        Identity identity,
        String walletId,
        String publicKey,
        String privateKey // Only returned once during creation
) {}