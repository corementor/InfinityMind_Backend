package io.corementor.infinitymind.service;

import io.corementor.infinitymind.exception.EmailNotFoundException;
import io.corementor.infinitymind.model.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email) throws EmailNotFoundException;

    User saveUser(User user);
}
