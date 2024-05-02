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
import org.mockito.Mockito;
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

    @Test
    void shouldReturnTheCorrectStoryGivenTheId() {
        var response = restTemplate
                .withBasicAuth("l@gmail.com", "password")
                .getForEntity("/api/stories/1", Story.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnStoryWhenValidStoryIdAndPrincipalProvided() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.lenient().when(principal.getName()).thenReturn("user@example.com");

        Long storyId = 1L;
        Story story = new Story();
        story.setId(storyId);
        Mockito.when(storyService.findStoryById(storyId)).thenReturn(story);

        ResponseEntity<Story> response = storyController.findStoryById(storyId, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(story);
    }

    @Test
    void shouldReturnUnauthorizedWhenStoryIdIsNull() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.lenient().when(principal.getName()).thenReturn("user@example.com");

        ResponseEntity<Story> response = storyController.findStoryById(null, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenNoPrincipalProvided() {
        ResponseEntity<Story> response = storyController.findStoryById(1L, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenStoryNotFound() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.lenient().when(principal.getName()).thenReturn("user@example.com");

        Long storyId = 1L;
        Mockito.lenient().when(storyService.findStoryById(storyId)).thenReturn(null);

        ResponseEntity<Story> response = storyController.findStoryById(storyId, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
