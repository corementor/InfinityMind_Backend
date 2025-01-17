package org.test.mindexpanseweb.service;

import org.test.mindexpanseweb.exception.EmailNotFoundException;
import org.test.mindexpanseweb.model.User;

import java.util.Optional;

public interface IUserService {
    Optional<User>findUserByUsername(String username);
    Optional<User> findUserByEmail(String email) throws EmailNotFoundException;
User saveUser(User user);
}
