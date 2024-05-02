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
import org.springframework.test.annotation.DirtiesContext;

import java.security.Principal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
    @DirtiesContext
    void shouldCreateAStory() {
        Principal principal = mock(Principal.class);
        User user = new User(1L, "test@gmail.com", "password", Role.ROLE_USER);
        Story story = new Story(1L, "Some Title", "Something small", user.getId(), new Date());
        Mockito.when(userService.findUserByEmail(principal.getName())).thenReturn(user);
        Mockito.when(storyService.createStory(story)).thenReturn(story);

        ResponseEntity<Story> response = storyController.createStory(story, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(story);
    }


}
