package org.test.mindexpanseweb.controller;

import jakarta.persistence.EntityExistsException;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.test.mindexpanseweb.dto.AuthResponse;
import org.test.mindexpanseweb.dto.LoginDto;
import org.test.mindexpanseweb.dto.Userdto;
import org.test.mindexpanseweb.model.User;
import org.test.mindexpanseweb.repository.IUserRepository;
import org.test.mindexpanseweb.service.AuthenticationService;
import org.test.mindexpanseweb.service.JwtService;
import org.test.mindexpanseweb.service.MyUserDetailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private AuthenticationService authService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailService userDetailsService;

    @GetMapping("/user-info")
    public Map<String, Object> user(/*@AuthenticationPrincipal OAuth2User principal*/) {

        Map<String, Object> userInfo = new HashMap<>();
        /*if (principal == null) {
            throw new EntityNotFoundException("No object found");
        }*/
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
                    .body(new AuthResponse(null, null, null, "Invalid username or password"));
        } catch (UsernameNotFoundException e) {
            System.out.println("Authentication failed - Username not found: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(null, null, null, "User not found"));
        } catch (Exception ex) {
            System.out.println("Authentication failed with unexpected error: " + ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, null, "An error occurred during authentication"));
        }
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