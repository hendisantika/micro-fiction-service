package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.model.UserRegistrationRequest;
import com.grayseal.microfictionapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

import static com.grayseal.microfictionapi.util.TextUtils.isValidRegistrationRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    private ResponseEntity<String> createUser(@RequestBody UserRegistrationRequest registrationRequest, UriComponentsBuilder ucb) {

        if (!isValidRegistrationRequest(registrationRequest)) {
            return ResponseEntity.badRequest().body("Invalid registration request");
        }

        User existingUser = userService.findUserByEmail(registrationRequest.getEmail());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Long userId = userService.registerUser(registrationRequest);
        if (userId != null) {
            URI uri = ucb.path("/api/users/{id}").buildAndExpand(userId).toUri();
            return ResponseEntity.created(uri).build();
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<User> findById(@PathVariable Long requestedId, Principal principal) {
        if (principal != null) {
            var authenticatedUser = userService.findUserByEmail(principal.getName());
            if (authenticatedUser != null && authenticatedUser.getId().equals(requestedId)) {
                return ResponseEntity.ok(authenticatedUser);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
