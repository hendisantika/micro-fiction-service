package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.Comment;
import com.grayseal.microfictionapi.service.CommentService;
import com.grayseal.microfictionapi.service.StoryService;
import com.grayseal.microfictionapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("api/comments")
@Tag(name = "Comments", description = "Endpoints for managing comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final StoryService storyService;

    public CommentController(CommentService commentService, UserService userService, StoryService storyService) {
        this.commentService = commentService;
        this.userService = userService;
        this.storyService = storyService;
    }

    @Operation(summary = "Create a new comment for a story")
    @PostMapping("/create")
    @ApiResponse(responseCode = "201", description = "Comment created successfully", content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment, Principal principal) {

    }

    @Operation(summary = "Retrieve a comment by ID")
    @GetMapping("/{commentId}")
    @ApiResponse(responseCode = "200", description = "Comment found", content = @Content(schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Comment> findCommentById(@PathVariable Long commentId, Principal principal) {

    }

    @Operation(summary = "Retrieve comments by story ID")
    @GetMapping("/story/{storyId}")
    @ApiResponse(responseCode = "200", description = "Comments found", content = @Content(schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<Comment>> getCommentsByStoryId(@PathVariable Long storyId, Principal principal) {

    }


    @Operation(summary = "Delete a comment by ID")
    @DeleteMapping("/{commentId}")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Principal principal) {

    }
}