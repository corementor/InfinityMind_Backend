package io.corementor.infinitymind.service;

import io.corementor.infinitymind.dto.AuthResponse;
import io.corementor.infinitymind.dto.LoginDto;
import io.corementor.infinitymind.dto.UserDto;

/**
 * The Interface IAuthenticationService.
 * @author Blaise Mugisha
 * @version 1.0
 */
public interface IAuthenticationService {
    /**
     * Register user.
     * @param userDto the UserDto
     * @return AuthResponse
     */
    AuthResponse registerUser(UserDto userDto);

    /**
     * Authenticate user.
     * @param loginDto the LoginDto
     * @return AuthResponse
     */
    AuthResponse authenticate(LoginDto loginDto);
}
