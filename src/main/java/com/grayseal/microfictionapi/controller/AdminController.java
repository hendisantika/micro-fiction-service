package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.repository.UserRepository;
import com.grayseal.microfictionapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.grayseal.microfictionapi.util.TextUtils.isValidRegistrationRequest;

@RestController
@RequestMapping("/api/users")
public class AdminController {
    private final UserRepository userRepository;

    private final UserService userService;

    private AdminController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/register-admin")
    private ResponseEntity<String> createAdmin(@RequestBody User admin, UriComponentsBuilder ucb) {

        if (!isValidRegistrationRequest(admin)) {
            return ResponseEntity.badRequest().body("Invalid registration request");
        }

        if (userService.findUserByEmail(admin.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (admin.getId() != null) {
            userService.registerAdmin(admin);
            URI uri = ucb.path("/api/users/{id}").buildAndExpand(admin.getId()).toUri();
            return ResponseEntity.created(uri).build();
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }
}
