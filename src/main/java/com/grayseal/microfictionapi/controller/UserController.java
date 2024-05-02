package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import static com.grayseal.microfictionapi.util.TextUtils.isValidRegistrationRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user, UriComponentsBuilder ucb) {

        if (!isValidRegistrationRequest(user)) {
            return ResponseEntity.badRequest().body("Invalid registration request");
        }

        if (userService.findUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (user.getId() != null) {
            userService.registerUser(user);
            URI uri = ucb.path("/api/users/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(uri).build();
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<User> findUserById(@PathVariable Long requestedId, Principal principal) {
        if (principal != null && requestedId != null) {
            if (userService.existsById(requestedId)) {
                var authenticatedUser = userService.findUserByEmail(principal.getName());
                if (authenticatedUser != null && authenticatedUser.getId().equals(requestedId)) {
                    return ResponseEntity.ok(authenticatedUser);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User serviceUserByEmailAndPassword = userService.findUserByEmailAndPassword(user.getEmail(), user.getPassword());
        if (serviceUserByEmailAndPassword != null) {
            return ResponseEntity.ok(serviceUserByEmailAndPassword);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers(Principal principal) {
        if (principal != null) {
            List<User> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
        if (id != null) {
            if (userService.existsById(id) && userService.findUserByEmail(principal.getName()).getId().equals(id)) {
                userService.deleteUserById(id);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
