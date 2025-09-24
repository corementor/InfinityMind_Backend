package io.corementor.infinitymind.service;

import io.corementor.infinitymind.exception.EmailNotFoundException;
import io.corementor.infinitymind.model.User;

import java.util.Optional;

/**
 * The Interface IUserService.
 * @author Blaise Mugisha
 * @version 1.0
 */
public interface IUserService {
    /**
     * Find user by username.
     * @param username String
     * @return Optional
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Find user by email.
     * @param email String
     * @return Optional
     * @throws EmailNotFoundException EmailNotFoundException
     */
    Optional<User> findUserByEmail(String email) throws EmailNotFoundException;

    /**
     * Save user.
     * @param user the User
     * @return User
     */
    User saveUser(User user);
}
