package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.controller.UserController;
import com.grayseal.microfictionapi.model.Role;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.model.UserCredentials;
import com.grayseal.microfictionapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private UriComponentsBuilder uriComponentsBuilder;

    @Mock
    UriComponents uriComponents;


    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    @DirtiesContext
    void shouldNotAllowInvalidEmails() {
        UserCredentials wrongEmailRequest = new UserCredentials("test", "password");
        ResponseEntity<String> createResponse = restTemplate
                .postForEntity("/api/users/register", wrongEmailRequest, String.class);
        assertThat(createResponse.getBody()).isEqualTo("Invalid registration request");
    }

    @Test
    @DirtiesContext
    void shouldCreateANewUserIfDetailsAreValid() {
        User user = new User(1L, "test@example.com", "password", Role.ROLE_USER);
        when(userService.registerUser(user)).thenReturn(user);

        URI expectedUri = URI.create("/api/users/1");
        when(uriComponentsBuilder.path("/api/users/{id}")).thenReturn(uriComponentsBuilder);
        when(uriComponentsBuilder.buildAndExpand(1L)).thenReturn(uriComponents);
        when(uriComponentsBuilder.buildAndExpand(1L).toUri()).thenReturn(expectedUri);

        ResponseEntity<String> response = userController.createUser(user, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(expectedUri);
        verify(userService).registerUser(user);
    }

    @Test
    @DirtiesContext
    void shouldNotCreateUserIfEmailExists() {
        User existingUser = new User(1L, "existing@example.com", "password", Role.ROLE_USER);

        when(userService.findUserByEmail("existing@example.com")).thenReturn(existingUser);

        ResponseEntity<String> response = userController.createUser(existingUser, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Email already exists");
        verify(userService, never()).registerUser(existingUser);
    }

    @Test
    void shouldReturnUnauthorizedWhenRequestedUserIdBelongsToDifferentUser() {
        Long requestedId = 2L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("authenticatedUser");
        when(userService.existsById(requestedId)).thenReturn(true);
        when(userService.findUserByEmail("authenticatedUser")).thenReturn(new User(1L, "authenticatedUser", "password", Role.ROLE_USER));

        ResponseEntity<User> response = userController.findUserById(requestedId, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService).existsById(requestedId);
        verify(userService).findUserByEmail("authenticatedUser");
    }

    @Test
    void shouldReturnUserWhenRequestedUserIdBelongsToAuthenticatedUser() {
        Long requestedId = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("authenticatedUser");
        when(userService.existsById(requestedId)).thenReturn(true);
        User authenticatedUser = new User(requestedId, "authenticatedUser", "password", Role.ROLE_USER);
        when(userService.findUserByEmail("authenticatedUser")).thenReturn(authenticatedUser);

        ResponseEntity<User> response = userController.findUserById(requestedId, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authenticatedUser);
        verify(userService).existsById(requestedId);
        verify(userService).findUserByEmail("authenticatedUser");
    }

    @Test
    @DirtiesContext
    void shouldAllowLoginIfEmailAndPasswordAreCorrect() {
        String email = "test@example.com";
        String password = "password123";
        User testUser = new User();
        testUser.setEmail(email);
        testUser.setPassword(password);

        when(userService.findUserByEmailAndPassword(email, password)).thenReturn(testUser);

        ResponseEntity<User> response = userController.login(new UserCredentials(email, password));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void shouldNotAllowLoginIfEmailOrPasswordAreInCorrect() {
        String email = "test@example.com";
        String password = "incorrectPassword";

        // Mock userService to return null, for invalid credentials
        when(userService.findUserByEmailAndPassword(email, password)).thenReturn(null);

        ResponseEntity<User> response = userController.login(new UserCredentials(email, password));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnAllUsersWhenListIsRequested() {
        Principal principal = mock(Principal.class);
        List<User> users = new ArrayList<>();
        var user = new User(2L, "test@example.com", "123", Role.ROLE_USER);
        users.add(user);
        users.add(new User(2L, "test2@example.com", "1234", Role.ROLE_USER));

        when(userService.findAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.findAllUsers(principal);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotDeleteUserIfIdBelongsToDifferentUser() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        when(userService.existsById(1L)).thenReturn(true);
        when(userService.findUserByEmail("test@example.com")).thenReturn(new User(2L, "another@example.com", "password", Role.ROLE_USER));

        ResponseEntity<Void> response = userController.deleteUser(1L, principal);

        // Verify that deleteUserById is not called
        verify(userService, never()).deleteUserById(anyLong());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteUserIfIdExistsAndBelongsToAuthenticatedUser() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        when(userService.existsById(1L)).thenReturn(true);
        when(userService.findUserByEmail("test@example.com")).thenReturn(new User(1L, "test@example.com", "password", Role.ROLE_USER));

        ResponseEntity<Void> response = userController.deleteUser(1L, principal);

        verify(userService).deleteUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
