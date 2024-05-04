package com.grayseal.microfictionapi.controller;

import com.grayseal.microfictionapi.model.Like;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.service.LikeService;
import com.grayseal.microfictionapi.service.StoryService;
import com.grayseal.microfictionapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/likes")
public class LikeController {

    private final StoryService storyService;
    private final UserService userService;
    private final LikeService likeService;

    public LikeController(StoryService storyService, UserService userService, LikeService likeService) {
        this.storyService = storyService;
        this.userService = userService;
        this.likeService = likeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Like> createLike(@RequestBody Like like, Principal principal) {
        if (principal != null && like != null) {
            User user = userService.findUserByEmail(principal.getName());
            if (user != null) {
                if (user.getId().equals(like.getUserId())) {
                    Like createdLike = likeService.createLike(like);
                    if (createdLike != null) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{likeId}")
    public ResponseEntity<Like> findLikeById(@PathVariable Long likeId, Principal principal) {
        if (principal != null && likeId != null) {
            Like like = likeService.findLikeById(likeId);
            if (like != null) {
                return ResponseEntity.ok(like);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Like>> getLikesByStoryId(@PathVariable Long storyId, Principal principal) {
        if (principal != null && storyId != null) {
            if (storyService.existsById(storyId)) {
                List<Like> likes = likeService.findLikesByStoryId(storyId);
                return ResponseEntity.ok(likes);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUserId(@PathVariable Long userId, Principal principal) {
        if (principal != null && userId != null) {
            if (userService.existsById(userId)) {
                List<Like> likes = likeService.findLikesByUserId(userId);
                return ResponseEntity.ok(likes);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long likeId, Principal principal) {
        if (principal != null && likeId != null) {
            User user = userService.findUserByEmail(principal.getName());
            Like like = likeService.findLikeById(likeId);
            if (like != null && user != null) {
                if (like.getUserId().equals(user.getId())) {
                    likeService.deleteLike(likeId);
                    return ResponseEntity.noContent().build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}
