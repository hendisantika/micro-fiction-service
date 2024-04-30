package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.controller.UserController;
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

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    @DirtiesContext
    void shouldNotAllowInvalidEmails() {
        UserCredentials wrongEmailRequest = new UserCredentials("lynne", "password");
        ResponseEntity<String> createResponse = restTemplate
                .postForEntity("/api/users/register", wrongEmailRequest, String.class);
        assertThat(createResponse.getBody()).isEqualTo("Invalid registration request");
    }

    @Test
    @DirtiesContext
    void shouldCreateANewUserIfDetailsAreValid() {
        UserCredentials request = new UserCredentials("l@gmail.com", "password");
        ResponseEntity<String> createResponse = restTemplate
                .postForEntity("/api/users/register", request, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI idOfUser = createResponse.getHeaders().getLocation();
        System.out.println("ID of the created user: " + idOfUser);
        ResponseEntity<User> getResponse = restTemplate
                .withBasicAuth("l@gmail.com", "password")
                .getForEntity(idOfUser, User.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void shouldNotCreateUserIfEmailExists() {
        UserCredentials userCredentials = new UserCredentials("l@gmail.com", "password");
        ResponseEntity<String> createResponse = restTemplate
                .postForEntity("/api/users/register", userCredentials, String.class);
        assertThat(createResponse.getBody()).isEqualTo("Email already exists");
    }

    @Test
    @DirtiesContext
    void shouldNotFetchUserIfTheUserIdIsNotTheAuthenticatedUser() {
        ResponseEntity<User> getResponse = restTemplate
                .withBasicAuth("l@gmail.com", "password")
                .getForEntity("/api/users/1", User.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void shouldAllowLoginIfEmailAndPasswordAreCorrect() {
        UserCredentials userCredentials = new UserCredentials("l@gmail.com", "password");
        ResponseEntity<String> response = restTemplate
                .postForEntity("/api/users/login", userCredentials, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void shouldNotAllowLoginIfEmailOrPasswordAreInCorrect() {
        UserCredentials userCredentials = new UserCredentials("l@gmail.com", "pass");
        ResponseEntity<String> response = restTemplate
                .postForEntity("/api/users/login", userCredentials, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
