package io.corementor.mindexpanse.service;

import io.corementor.mindexpanse.exception.EmailNotFoundException;
import io.corementor.mindexpanse.model.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email) throws EmailNotFoundException;

    User saveUser(User user);
}
