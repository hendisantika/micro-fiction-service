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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

//    @Test
//    void shouldReturnTheCorrectStoryGivenTheId() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/stories/1", Story.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

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

//    @Test
//    void shouldReturnAListOfStoriesForUser() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/stories/user/5?page=0&size=1", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        System.out.println(response.getBody());
//    }

    @Test
    void shouldReturnUnauthorizedWhenUserIdIsNull() {
        Principal principal = Mockito.mock(Principal.class);

        ResponseEntity<List<Story>> response = storyController.findAllUserStories(null, null, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(userService, never()).existsById(anyLong());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotAuthenticated() {
        Long userId = 123L;

        ResponseEntity<List<Story>> response = storyController.findAllUserStories(userId, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(userService, never()).existsById(anyLong());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotFound() {
        Principal principal = Mockito.mock(Principal.class);
        Long userId = 123L;
        when(userService.existsById(userId)).thenReturn(false);

        ResponseEntity<List<Story>> response = storyController.findAllUserStories(userId, null, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(userService).existsById(userId);
    }

    @Test
    void shouldReturnStoriesWhenUserExists() {
        Principal principal = Mockito.mock(Principal.class);
        Long userId = 123L;
        Pageable pageable = Mockito.mock(Pageable.class);
        List<Story> stories = new ArrayList<>();
        stories.add(new Story());
        Page<Story> storyPage = new PageImpl<>(stories);

        when(userService.existsById(userId)).thenReturn(true);

        when(storyService.findAllStoriesByUser(userId, pageable)).thenReturn(storyPage);

        ResponseEntity<List<Story>> response = storyController.findAllUserStories(userId, pageable, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(storyService).findAllStoriesByUser(userId, pageable);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(stories);
    }

//    @Test
//    void shouldReturnAListOfStoriesIfUserIsAuthenticated() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/stories", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        System.out.println(response.getBody());
//    }

    @Test
    void shouldReturnStoriesWhenPrincipalIsNotNull() {
        Principal principal = Mockito.mock(Principal.class);
        Pageable pageable = Mockito.mock(Pageable.class);
        List<Story> storyList = new ArrayList<>();
        storyList.add(new Story());
        Page<Story> page = new PageImpl<>(storyList);

        when(storyService.findAllStories(pageable)).thenReturn(page);

        ResponseEntity<List<Story>> response = storyController.findAllStories(principal, pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(storyList);
        verify(storyService).findAllStories(pageable);
    }

//    @Test
//    void shouldUpdateAStorySuccessfully() {
//        Story story = new Story(1L, "Some New Title", "Some new content", 5L, new Date());
//        HttpEntity<Story> request = new HttpEntity<>(story);
//        ResponseEntity<Story> response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .exchange("/api/stories/1", HttpMethod.PUT, request, Story.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldReturnUnauthorizedIfPrincipalIsNullInStoryUpdate() {
        Long storyId = 1L;
        Story updatedStory = new Story();
        Principal principal = null;

        ResponseEntity<Story> response = storyController.updateStory(storyId, updatedStory, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(storyService);
    }

    @Test
    void shouldReturnUnauthorizedIfStoryIdIsNull() {
        Story updatedStory = new Story();
        Principal principal = Mockito.mock(Principal.class);

        ResponseEntity<Story> response = storyController.updateStory(null, updatedStory, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(storyService);
    }

    @Test
    void shouldReturnUnauthorizedIfUserNotFoundInStoryUpdate() {
        Long storyId = 1L;
        Story updatedStory = new Story();
        Principal principal = Mockito.mock(Principal.class);
        lenient().when(userService.findUserByEmail(anyString())).thenReturn(null);

        ResponseEntity<Story> response = storyController.updateStory(storyId, updatedStory, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService).findUserByEmail(principal.getName());
        verifyNoInteractions(storyService);
    }

    @Test
    void shouldReturnUnauthorizedIfUserIdMismatchInStoryUpdate() {
        Long storyId = 1L;
        Story updatedStory = new Story();
        updatedStory.setUserId(2L);
        Principal principal = Mockito.mock(Principal.class);
        User user = new User();
        user.setId(1L);
        lenient().when(userService.findUserByEmail(anyString())).thenReturn(user);

        ResponseEntity<Story> response = storyController.updateStory(storyId, updatedStory, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService).findUserByEmail(principal.getName());
        verifyNoInteractions(storyService);
    }

    @Test
    void shouldReturnStoryWhenUpdatedSuccessfully() {
        Long storyId = 1L;
        Story updatedStory = new Story();
        updatedStory.setUserId(1L);
        Principal principal = Mockito.mock(Principal.class);
        User user = new User();
        user.setId(1L);
        lenient().when(userService.findUserByEmail(principal.getName())).thenReturn(user);
        when(storyService.updateStory(storyId, updatedStory)).thenReturn(updatedStory);

        ResponseEntity<Story> response = storyController.updateStory(storyId, updatedStory, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedStory);
        verify(userService).findUserByEmail(principal.getName());
        verify(storyService).updateStory(storyId, updatedStory);
    }


}
