package org.test.mindexpanseweb.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.test.mindexpanseweb.model.User;
import org.test.mindexpanseweb.repository.IUserRepository;

import java.util.Optional;

import static io.jsonwebtoken.security.Keys.password;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

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
