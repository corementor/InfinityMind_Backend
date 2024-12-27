package org.test.mindexpanseweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.test.mindexpanseweb.dto.Userdto;
import org.test.mindexpanseweb.model.User;
import org.test.mindexpanseweb.repository.UserRepository;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @PostMapping
    String createUser(@RequestBody Userdto userDetails) {
        User user = new User();
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        userRepository.save(user);
        return "User created Successfully";
    }

    @GetMapping
    List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @DeleteMapping("/{id}")
    String deleteUser(@PathVariable UUID id){
        userRepository.deleteById(id);
        return "User deleted Successfully";
    }

}
