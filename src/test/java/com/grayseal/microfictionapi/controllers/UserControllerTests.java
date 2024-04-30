package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.model.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DirtiesContext
    void shouldNotAllowInvalidEmails() {
        UserRegistrationRequest wrongEmailRequest = new UserRegistrationRequest("lynne", "password");
        ResponseEntity<String> createResponse = restTemplate
                .postForEntity("/api/users/register", wrongEmailRequest, String.class);
        assertThat(createResponse.getBody()).isEqualTo("Invalid registration request");
    }

    @Test
    @DirtiesContext
    void shouldCreateANewUserIfDetailsAreValid() {
        UserRegistrationRequest request = new UserRegistrationRequest("l@gmail.com", "password");
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
    void shouldNotFetchUserIfTheUserIdIsNotTheAuthenticatedUser() {
        ResponseEntity<User> getResponse = restTemplate
                .withBasicAuth("l@gmail.com", "password")
                .getForEntity("/api/users/1", User.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
