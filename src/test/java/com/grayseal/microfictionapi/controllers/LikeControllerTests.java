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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

//    @Test
//    void shouldReturnAListOfLikesIfStoryIdIsValid() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/likes/story/4", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldReturnLikesByStoryId() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        List<Like> likes = new ArrayList<>();
        Like like1 = new Like();
        like1.setId(1L);
        like1.setUserId(1L);
        like1.setStoryId(1L);
        likes.add(like1);

        Like like2 = new Like();
        like2.setId(2L);
        like2.setUserId(2L);
        like2.setStoryId(1L);
        likes.add(like2);

        when(storyService.existsById(1L)).thenReturn(true);
        when(likeService.findLikesByStoryId(1L)).thenReturn(likes);

        ResponseEntity<List<Like>> response = likeController.getLikesByStoryId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(likes);
    }

    @Test
    void shouldReturnUnauthorizedWhenStoryIdIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        ResponseEntity<List<Like>> response = likeController.getLikesByStoryId(null, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenStoryNotFound() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        when(storyService.existsById(1L)).thenReturn(false);

        ResponseEntity<List<Like>> response = likeController.getLikesByStoryId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void shouldReturnAListOfLikesIfUserIdIsValid() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/likes/user/5", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldReturnLikesByUserId() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        List<Like> likes = new ArrayList<>();
        Like like1 = new Like();
        like1.setId(1L);
        like1.setUserId(1L);
        like1.setStoryId(1L);
        likes.add(like1);

        Like like2 = new Like();
        like2.setId(2L);
        like2.setUserId(1L);
        like2.setStoryId(2L);
        likes.add(like2);

        when(userService.existsById(1L)).thenReturn(true);
        when(likeService.findLikesByUserId(1L)).thenReturn(likes);

        ResponseEntity<List<Like>> response = likeController.getLikesByUserId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(likes);
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIdIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        ResponseEntity<List<Like>> response = likeController.getLikesByUserId(null, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void shouldDeleteAlikeSuccessfully() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .exchange("/api/likes/1", HttpMethod.DELETE, null, Void.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//    }

    @Test
    void shouldDeleteLikeSuccessfully() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        User user = new User();
        user.setId(1L);
        when(userService.findUserByEmail(principal.getName())).thenReturn(user);

        Like like = new Like();
        like.setId(1L);
        like.setUserId(1L);
        when(likeService.findLikeById(1L)).thenReturn(like);

        ResponseEntity<Void> response = likeController.deleteLike(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldReturnNotFoundWhenLikeNotFound() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        User user = new User();
        user.setId(1L);
        when(userService.findUserByEmail(principal.getName())).thenReturn(user);

        lenient().when(likeService.findLikeById(1L)).thenReturn(null);

        ResponseEntity<Void> response = likeController.deleteLike(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
