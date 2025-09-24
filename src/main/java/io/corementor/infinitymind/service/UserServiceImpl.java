package io.corementor.infinitymind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.corementor.infinitymind.exception.EmailNotFoundException;
import io.corementor.infinitymind.model.User;
import io.corementor.infinitymind.repository.IUserRepository;

import java.util.Optional;

/**
 * The class User Service Impl.
 * @author Blaise Mugisha
 * @version 1.0
 */
@Service
public class UserServiceImpl implements IUserService {
    /**
     * The user repository.
     */
    @Autowired
    private IUserRepository userRepository;

    /**
     * Find user by username.
     * @param username String
     * @return Optional<User>
     */
    public Optional<User> findUserByUsername(String username) {
        Optional<User> user = userRepository.findUsersByUsername(username);
        return Optional.ofNullable(user.orElseThrow(() -> new UsernameNotFoundException("User with username ::" + username + " not found.")));
    }

    /**
     * Find user by email.
     * @param email String
     * @return Optional<User>
     * @throws EmailNotFoundException EmailNotFoundException
     */
    @Override
    public Optional<User> findUserByEmail(String email) throws EmailNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return Optional.ofNullable(user.orElseThrow(() -> new EmailNotFoundException("Email not found" + email)));
    }

    /**
     * Save user.
     * @param user the User
     * @return User
     */
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
