package org.test.mindexpanseweb.service;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.test.mindexpanseweb.dto.AuthResponse;
import org.test.mindexpanseweb.dto.LoginDto;
import org.test.mindexpanseweb.dto.Userdto;
import org.test.mindexpanseweb.model.User;
import org.test.mindexpanseweb.repository.IUserRepository;

import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public AuthResponse registerUser(Userdto userdto) {
        validateUserDto(userdto);
        String email = userdto.getEmail();

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new EntityExistsException("Email already exists");
        }

        User user = new User();
        user.setFirstName(userdto.getFirstName());
        user.setLastName(userdto.getLastName());
        String username = createUsername(userdto);
        user.setUsername(username);
        user.setEmail(userdto.getEmail());
        user.setPassword(passwordEncoder.encode(userdto.getPassword()));
        userRepository.save(user);

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), username, user.getFirstName());
        } catch (MessagingException e) {
            // Log the error but don't prevent user registration
            // Consider implementing a retry mechanism or queueing system
            e.printStackTrace();
        }
        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getFirstName() + " " + user.getLastName(), user.getEmail(),username, token);
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

    public AuthResponse authenticate(LoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Then fetch the user
        User user = userRepository.findUsersByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                token
        );
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


}
