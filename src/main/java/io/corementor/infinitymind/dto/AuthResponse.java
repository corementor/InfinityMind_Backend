package io.corementor.infinitymind.dto;


import lombok.Data;

@Data
public class AuthResponse {
    private String names;
    private String email;
    private String username;
    private String accessToken;
    private String refreshToken;
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