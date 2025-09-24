package io.corementor.infinitymind.service;

import io.corementor.infinitymind.dto.RefreshTokenRequest;
import io.corementor.infinitymind.dto.UserDto;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.corementor.infinitymind.dto.AuthResponse;
import io.corementor.infinitymind.dto.LoginDto;
import io.corementor.infinitymind.model.User;
import io.corementor.infinitymind.repository.IUserRepository;

import java.util.Optional;

/**
 * The class Authentication Service.
 *
 * @author Blaise Mugisha
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class AuthenticationService implements IAuthenticationService {
    /**
     * The user repository.
     */
    private final IUserRepository userRepository;
    /**
     * The jwt service.
     */
    private final JwtService jwtService;
    /**
     * The authentication manager.
     */
    private final AuthenticationManager authenticationManager;
    /**
     * The password encoder.
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * The email service.
     */
    private final EmailService emailService;

    /**
     * Authenticate the user.
     * @param request the LoginDto
     * @return AuthResponse
     */
    public AuthResponse authenticate(LoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findUsersByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                accessToken,
                refreshToken,
                "Login Successfully"
        );
    }

    /**
     * Refresh the token.
     * @param request the RefreshTokenRequest
     * @return AuthResponse
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new IllegalArgumentException("Refresh token is required");
            }

            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("Invalid refresh token type");
            }

            if (jwtService.isTokenExpired(refreshToken)) {
                throw new IllegalArgumentException("Refresh token has expired");
            }

            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findUsersByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Validate refresh token against user
            if (!jwtService.isValid(refreshToken, user)) {
                throw new IllegalArgumentException("Invalid refresh token for user");
            }

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            return new AuthResponse(
                    user.getFirstName() + " " + user.getLastName(),
                    user.getEmail(),
                    user.getUsername(),
                    newAccessToken,
                    newRefreshToken,
                    "Tokens refreshed successfully"
            );

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            System.err.println("Unexpected error during token refresh: " + e.getMessage());
            throw new RuntimeException("Token refresh failed due to server error");
        }
    }

    /**
     * Create the username.
     * @param userDto the UserDto
     * @return String
     */
    public String createUsername(UserDto userDto) {
        String baseUsername = (userDto.getFirstName().toLowerCase() + "." + userDto.getLastName().toLowerCase()).replaceAll("\\s+", "");
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.findUsersByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }
        return username;
    }

    /**
     * Register the user.
     * @param userDto the UserDto
     * @return AuthResponse
     */
    @Override
    public AuthResponse registerUser(UserDto userDto) {
        validateUserDto(userDto);
        String email = userDto.getEmail().toLowerCase();

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new EntityExistsException("Email already exists");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        String username = createUsername(userDto);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);

        try {
            emailService.sendWelcomeEmail(user.getEmail(), username, user.getFirstName());
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                username,
                accessToken,
                refreshToken,
                "Account registered successfully"
        );
    }

    /**
     * Validate the userDto.
     * @param userDto the UserDto
     */

    private void validateUserDto(UserDto userDto) {
        if (userDto.getFirstName() == null || userDto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (userDto.getLastName() == null || userDto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

}
