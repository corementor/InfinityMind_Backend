package io.corementor.infinitymind.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.corementor.infinitymind.model.User;

import java.util.Optional;
/**
 * The interface IUserRepository.
 *
 * @author Blaise Mugisha.
 * @version 1.0
 */

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    /**
     * Find by id and state
     *
     * @param email the string
     * @return optional
     */

    Optional<User> findByEmail(String email);
    /**
     * Find by username
     *
     * @param username the string
     * @return optional
     */
    Optional<User> findUsersByUsername(String username);
}