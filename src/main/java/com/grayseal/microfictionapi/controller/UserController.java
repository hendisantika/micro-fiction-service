package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
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

    @Operation(summary = "Retrieve a user by ID")
    @GetMapping("/{requestedId}")
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
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

    @Operation(summary = "Login user")
    @PostMapping("/login")
    @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<User> login(@RequestBody User user) {
        User serviceUserByEmailAndPassword = userService.findUserByEmailAndPassword(user.getEmail(), user.getPassword());
        if (serviceUserByEmailAndPassword != null) {
            return ResponseEntity.ok(serviceUserByEmailAndPassword);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Retrieve all users")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Users found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<User>> findAllUsers(Principal principal) {
        if (principal != null) {
            List<User> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
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
