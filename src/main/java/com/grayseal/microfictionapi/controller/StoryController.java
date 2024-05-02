package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.Story;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.StoryService;
import com.grayseal.microfictionapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/stories")
@Tag(name = "Stories", description = "Endpoints for managing stories")
public class StoryController {

    private final StoryService storyService;
    private final UserService userService;

    public StoryController(StoryService storyService, UserService userService) {
        this.storyService = storyService;
        this.userService = userService;
    }


    @Operation(summary = "Create a new story")
    @PostMapping("/create")
    @ApiResponse(responseCode = "201", description = "Story created successfully", content = @Content(schema = @Schema(implementation = Story.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Story> createStory(@RequestBody Story story, Principal principal) {
        if (principal != null && story != null) {
            User user = userService.findUserByEmail(principal.getName());
            if (user != null) {
                if (user.getId().equals(story.getUserId())) {
                    Story createdStory = storyService.createStory(story);
                    if (createdStory != null) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(createdStory);
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Retrieve a story by ID")
    @GetMapping("/{storyId}")
    @ApiResponse(responseCode = "200", description = "Story found", content = @Content(schema = @Schema(implementation = Story.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Story> findStoryById(@PathVariable Long storyId, Principal principal) {
        if (principal != null && storyId != null) {
            Story story = storyService.findStoryById(storyId);
            if (story != null) {
                return ResponseEntity.ok(story);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Retrieve all stories belonging to a specific user")
    @GetMapping("/user/{userId}")
    @ApiResponse(responseCode = "200", description = "Stories found", content = @Content(schema = @Schema(implementation = Story.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<Story>> findAllUserStories(@PathVariable Long userId, Pageable pageable, Principal principal) {
        if (principal != null && userId != null) {
            if (userService.existsById(userId)) {
                Page<Story> stories = storyService.findAllStoriesByUser(userId, pageable);
                return ResponseEntity.ok(stories.getContent());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Retrieve all stories")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Stories found", content = @Content(schema = @Schema(implementation = Story.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<Story>> findAllStories(Principal principal, Pageable pageable) {
        if (principal != null) {
            Page<Story> stories = storyService.findAllStories(pageable);
            return ResponseEntity.ok(stories.getContent());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Update an existing story")
    @PutMapping("/{storyId}")
    @ApiResponse(responseCode = "200", description = "Story updated successfully", content = @Content(schema = @Schema(implementation = Story.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Story> updateStory(@PathVariable Long storyId, @RequestBody Story updatedStory, Principal principal) {
        if (principal != null && storyId != null) {
            User user = userService.findUserByEmail(principal.getName());
            if (user != null && user.getId().equals(updatedStory.getUserId())) {
                var story = storyService.updateStory(storyId, updatedStory);
                if (story != null) {
                    return ResponseEntity.ok(story);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Delete a story")
    @DeleteMapping("/{storyId}")
    @ApiResponse(responseCode = "204", description = "Story deleted successfully")
    @ApiResponse(responseCode = "404", description = "Story not found")
    public ResponseEntity<Void> deleteStory(@PathVariable Long storyId, Principal principal) {
        if (principal != null & storyId != null) {
            User user = userService.findUserByEmail(principal.getName());
            if (storyService.existsById(storyId)) {
                Story story = storyService.findStoryById(storyId);
                if (story != null && story.getUserId().equals(user.getId())) {
                    storyService.deleteStory(storyId);
                    return ResponseEntity.noContent().build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}