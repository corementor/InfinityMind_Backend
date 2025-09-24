package io.corementor.infinitymind.dto;


import lombok.*;

/**
 * The Class AuthResponse.
 *
 * @author Blaise Mugisha.
 * @version 1.0
 */
@Data
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class AuthResponse {
    /**
     * The names.
     */
    private String names;
    /**
     * The email.
     */
    private String email;
    /**
     * The username.
     */
    private String username;
    /**
     * The accessToken.
     */
    private String accessToken;
    /**
     * The refreshToken.
     */
    private String refreshToken;
    /**
     * The message.
     */
    private String message;

    public AuthResponse(String names, String email, String username, String accessToken, String refreshToken,String message) {
        this.names = names;
        this.email = email;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message=message;
    }

    // Keep old constructor for backward compatibility
    public AuthResponse(String names, String email, String accessToken) {
        this.names = names;
        this.email = email;
        this.accessToken = accessToken;
    }
}