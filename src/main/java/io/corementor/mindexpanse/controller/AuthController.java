package io.corementor.mindexpanse.controller;

import io.corementor.mindexpanse.dto.RefreshTokenRequest;
import io.corementor.mindexpanse.model.User;
import jakarta.persistence.EntityExistsException;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import io.corementor.mindexpanse.dto.AuthResponse;
import io.corementor.mindexpanse.dto.LoginDto;
import io.corementor.mindexpanse.dto.Userdto;
import io.corementor.mindexpanse.repository.IUserRepository;
import io.corementor.mindexpanse.service.AuthenticationService;
import io.corementor.mindexpanse.service.JwtService;
import io.corementor.mindexpanse.service.MyUserDetailService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final IUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginRequest) {
        try {
            System.out.println("Login attempt with username: " + loginRequest.getUsername());
            AuthResponse response = authService.authenticate(loginRequest);
            System.out.println("Authentication successful for user: " + loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed - Bad credentials for username: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new
                            AuthResponse(null, null,null, null,null, "Invalid username or password"));
        } catch (UsernameNotFoundException e) {
            System.out.println("Authentication failed - Username not found: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(null, null,null, null,null, "User not found"));
        } catch (Exception ex) {
            System.out.println("Authentication failed with unexpected error: " + ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null,null, null,null, "An error occurred during authentication"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "No valid token provided"));
            }

            String token = authHeader.substring(7);

            // Check if token is expired
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Token expired"));
            }

            // Check if it's an access token
            if (!jwtService.isAccessToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Invalid token type"));
            }

            String username = jwtService.extractUsername(token);
            User user = userRepository.findUsersByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtService.isValid(token, user)) {
                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "message", "Token is valid"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Token is invalid"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token validation failed"));
        }
    }

    @GetMapping("/user-info")
    public Map<String, Object> user(/*@AuthenticationPrincipal OAuth2User principal*/) {
        Map<String, Object> userInfo = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else if (authentication != null && authentication.isAuthenticated()) {
            if (authentication instanceof OAuth2User oauth2User) {
                userInfo.putAll(oauth2User.getAttributes());
            }
            return userInfo;
        }
        throw new EntityNotFoundException("No object found");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Userdto userdto) {
        try {
            return ResponseEntity.ok(authService.registerUser(userdto));
        } catch (EntityExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already exists."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred. Please try again later."));
        }
    }
}