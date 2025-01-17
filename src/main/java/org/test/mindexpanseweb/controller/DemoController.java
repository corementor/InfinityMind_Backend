package org.test.mindexpanseweb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class DemoController {
    @RequestMapping("/hello")
    public String home() {
        return "Welcome!";
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
}
