package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.controller.LikeController;
import com.grayseal.microfictionapi.model.Like;
import com.grayseal.microfictionapi.model.Role;
import com.grayseal.microfictionapi.model.Story;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.LikeService;
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
import org.springframework.test.annotation.DirtiesContext;

import java.security.Principal;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LikeControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private StoryService storyService;

    @Mock
    private LikeService likeService;

    private LikeController likeController;

    @BeforeEach
    void setUp() {
        likeController = new LikeController(storyService, userService, likeService);
    }

//    @Test
//    void shouldCreateLikeSuccessfully() {
//        Story story = new Story(4L, "Some New Title", "Some new content", 5L, new Date());
//        ResponseEntity<Story> createStoryResponse = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .postForEntity("/api/stories/create", story, Story.class);
//        assertThat(createStoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//        Like like = new Like(1L, 5L, 4L);
//        ResponseEntity<Like> createLikeResponse = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .postForEntity("/api/likes/create", like, Like.class);
//        assertThat(createLikeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }

    @Test
    @DirtiesContext
    void shouldCreateLikeSuccessfully() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@gmail.com");

        User user = new User(1L, "test@gmail.com", "password", Role.ROLE_USER);
        when(userService.findUserByEmail(principal.getName())).thenReturn(user);

        Story story = new Story(1L, "Test Story", "Once upon a time...", 1L, new Date());
        lenient().when(storyService.createStory(story)).thenReturn(story);

        Like like = new Like(1L, 1L, 1L);
        when(likeService.createLike(like)).thenReturn(like);

        ResponseEntity<Like> response = likeController.createLike(like, principal);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(like);
    }


    @Test
    void shouldReturnUnauthorizedWhenPrincipalIsNull() {
        ResponseEntity<Like> response = likeController.createLike(new Like(), null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenLikeIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");
        ResponseEntity<Like> response = likeController.createLike(null, principal);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@gmail.com");
        when(userService.findUserByEmail(principal.getName())).thenReturn(null);

        ResponseEntity<Like> response = likeController.createLike(new Like(), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void ShouldReturnLikeIfIdIsValid() {
//        ResponseEntity<Like> response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/likes/1", Like.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldReturnLikeIfIdIsValid() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        Like like = new Like();
        like.setId(1L);
        like.setUserId(1L);
        like.setStoryId(1L);
        when(likeService.findLikeById(1L)).thenReturn(like);

        ResponseEntity<Like> response = likeController.findLikeById(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(like);
    }

    @Test
    void shouldReturnUnauthorizedWhenIdIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        ResponseEntity<Like> response = likeController.findLikeById(null, principal);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenLikeNotFound() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        lenient().when(likeService.findLikeById(1L)).thenReturn(null);

        ResponseEntity<Like> response = likeController.findLikeById(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
