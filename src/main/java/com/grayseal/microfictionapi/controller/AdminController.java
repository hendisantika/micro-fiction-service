package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admins", description = "Endpoints for managing administrators")
public class AdminController {

    private final UserService userService;

    private AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new admin")
    @PostMapping("/register-admin")
    @ApiResponse(responseCode = "201", description = "Admin registered successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
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
