package io.corementor.infinitymind.dto;

import lombok.Data;

/**
 * The Class RefreshTokenRequest.
 * @author Blaise Mugisha
 * @version 1.0
 */
@Data
public class RefreshTokenRequest {
    /**
     * The refresh token.
     */
    private String refreshToken;
}
