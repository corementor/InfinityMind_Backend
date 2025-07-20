package io.corementor.mindexpanse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.corementor.mindexpanse.exception.EmailNotFoundException;
import io.corementor.mindexpanse.model.User;
import io.corementor.mindexpanse.repository.IUserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    public Optional<User> findUserByUsername(String username) {
        Optional<User> user = userRepository.findUsersByUsername(username);
        return Optional.ofNullable(user.orElseThrow(() -> new UsernameNotFoundException("User with username ::" + username + " not found.")));
    }

    @Override
    public Optional<User> findUserByEmail(String email) throws EmailNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return Optional.ofNullable(user.orElseThrow(() -> new EmailNotFoundException("Email not found" + email)));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
