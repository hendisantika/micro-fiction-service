package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.controller.CommentController;
import com.grayseal.microfictionapi.model.Comment;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.CommentService;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private StoryService storyService;

    @Mock
    private CommentService commentService;

    private CommentController commentController;

    @BeforeEach
    void setUp() {
        commentController = new CommentController(commentService, userService, storyService);
    }

//    @Test
//    void shouldCreateACommentSuccessfully() {
//        Comment comment = new Comment(1L, "Some attached comment", 5L, 1L);
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .postForEntity("/api/comments/create", comment, Comment.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }

    @Test
    void shouldCreateCommentSuccessfully() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        Comment comment = new Comment();
        comment.setUserId(1L);

        User user = new User();
        user.setId(1L);

        when(userService.findUserByEmail(principal.getName())).thenReturn(user);
        when(commentService.createComment(comment)).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.createComment(comment, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(comment);

        verify(userService, times(1)).findUserByEmail(principal.getName());
        verify(commentService, times(1)).createComment(comment);
    }

    @Test
    void shouldReturnUnauthorizedWhenPrincipalIsNull() {
        ResponseEntity<Comment> response = commentController.createComment(new Comment(), null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenCommentIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");
        ResponseEntity<Comment> response = commentController.createComment(null, principal);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotFound() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");
        when(userService.findUserByEmail(principal.getName())).thenReturn(null);

        ResponseEntity<Comment> response = commentController.createComment(new Comment(), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void shouldFetchCommentIfCommentIdIsValid() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/comments/1", Comment.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldReturnCommentIfIdIsValid() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        Comment comment = new Comment();
        comment.setId(1L);

        when(commentService.findCommentById(1L)).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.findCommentById(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(comment);
    }

    @Test
    void shouldReturnUnauthorizedWhenIdIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        ResponseEntity<Comment> response = commentController.findCommentById(null, principal);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenCommentNotFound() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        when(commentService.findCommentById(1L)).thenReturn(null);

        ResponseEntity<Comment> response = commentController.findCommentById(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void shouldReturnAListOfCommentsGivenTheStoryId() {
//        var response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .getForEntity("/api/comments/story/1", String.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void shouldReturnCommentsByStoryId() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setStoryId(1L);
        comments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setStoryId(1L);
        comments.add(comment2);

        when(storyService.existsById(1L)).thenReturn(true);
        when(commentService.findCommentsByStoryId(1L)).thenReturn(comments);

        ResponseEntity<List<Comment>> response = commentController.findCommentsByStoryId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(comments);
    }

    @Test
    void shouldReturnUnauthorizedWhenStoryIdIsNull() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        ResponseEntity<List<Comment>> response = commentController.findCommentsByStoryId(null, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedWhenStoryNotFound() {
        Principal principal = mock(Principal.class);
        lenient().when(principal.getName()).thenReturn("test@gmail.com");

        when(storyService.existsById(1L)).thenReturn(false);

        ResponseEntity<List<Comment>> response = commentController.findCommentsByStoryId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
