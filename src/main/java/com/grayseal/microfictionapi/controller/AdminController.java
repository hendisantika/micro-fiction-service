package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.model.UserCredentials;
import com.grayseal.microfictionapi.repository.UserRepository;
import com.grayseal.microfictionapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ResponseEntity<String> createUser(@RequestBody UserCredentials registrationRequest) {

        if (!isValidRegistrationRequest(registrationRequest)) {
            return ResponseEntity.badRequest().body("Invalid registration request");
        }
        // Validate user data (e.g., email format, password strength)
        User existingUser = userRepository.findByEmail(registrationRequest.getEmail());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Admin already exists");
        }

        if (userService.registerAdmin(registrationRequest)) {
            return ResponseEntity.ok("User created successfully");
        }
        return ResponseEntity.badRequest().body("Something went wrong!");
    }
}
