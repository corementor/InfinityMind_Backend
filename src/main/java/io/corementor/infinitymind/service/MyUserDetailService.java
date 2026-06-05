package io.corementor.infinitymind.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.corementor.infinitymind.model.User;
import io.corementor.infinitymind.repository.IUserRepository;

import java.util.Optional;

/**
 * The class My User Detail Service.
 * @author Blaise Mugisha
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class MyUserDetailService implements UserDetailsService {
    /**
     * The user repository.
     */
    private  final IUserRepository userRepository;

    /**
     * Load user by username.
     * @param username the username
     * @return the user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User>user=userRepository.findUsersByUsername(username);
        if (user.isPresent()){
         var userObj=user.get();
         return org.springframework.security.core.userdetails.User.builder()
                 .username(userObj.getUsername())
                 .password(userObj.getPassword())
                 .build();
        }else{
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
