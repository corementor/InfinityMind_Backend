package io.corementor.infinitymind.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.corementor.infinitymind.model.User;
import io.corementor.infinitymind.repository.IUserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyUserDetailService implements UserDetailsService {
    private  final IUserRepository userRepository;

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
