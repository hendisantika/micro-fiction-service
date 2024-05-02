package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.controller.StoryController;
import com.grayseal.microfictionapi.model.Role;
import com.grayseal.microfictionapi.model.Story;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.StoryService;
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

import java.security.Principal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoryControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private StoryService storyService;

    private StoryController storyController;

    @BeforeEach
    void setUp() {
        storyController = new StoryController(storyService, userService);
    }

    @Test
    void shouldCreateStorySuccessfully() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@gmail.com");

        User user = new User(1L, "test@gmail.com", "password", Role.ROLE_USER);
        when(userService.findUserByEmail(principal.getName())).thenReturn(user);

        Story story = new Story(1L, "Test Story", "Once upon a time...", 1L, new Date());

        when(storyService.createStory(story)).thenReturn(story);

        ResponseEntity<Story> response = storyController.createStory(story, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(story);
    }

    @Test
    void shouldReturnUnauthorizedIfPrincipalIsNull() {
        ResponseEntity<Story> response = storyController.createStory(new Story(), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedIfUserNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@gmail.com");

        when(userService.findUserByEmail(principal.getName())).thenReturn(null);

        ResponseEntity<Story> response = storyController.createStory(new Story(), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedIfUserIdMismatch() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@gmail.com");

        User user = new User(1L, "test@gmail.com", "password", Role.ROLE_USER);
        when(userService.findUserByEmail(principal.getName())).thenReturn(user);

        Story story = new Story(1L, "Test Story", "Once upon a time...", 2L, new Date());

        ResponseEntity<Story> response = storyController.createStory(story, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
