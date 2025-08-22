package io.corementor.mindexpanse.service;

import io.corementor.mindexpanse.dto.RefreshTokenRequest;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.corementor.mindexpanse.dto.AuthResponse;
import io.corementor.mindexpanse.dto.LoginDto;
import io.corementor.mindexpanse.dto.Userdto;
import io.corementor.mindexpanse.model.User;
import io.corementor.mindexpanse.repository.IUserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationService implements IAuthenticationService {

    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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


    public String createUsername(Userdto userdto) {
        String baseUsername = (userdto.getFirstName().toLowerCase() + "." + userdto.getLastName().toLowerCase()).replaceAll("\\s+", "");
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.findUsersByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }
        return username;
    }
    @Override
    public AuthResponse registerUser(Userdto userdto) {
        validateUserDto(userdto);
        String email = userdto.getEmail().toLowerCase();

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new EntityExistsException("Email already exists");
        }

        User user = new User();
        user.setFirstName(userdto.getFirstName());
        user.setLastName(userdto.getLastName());
        String username = createUsername(userdto);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(userdto.getPassword()));
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

    private void validateUserDto(Userdto userdto) {
        if (userdto.getFirstName() == null || userdto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (userdto.getLastName() == null || userdto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (userdto.getEmail() == null || userdto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (userdto.getPassword() == null || userdto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

}
