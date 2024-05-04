package com.grayseal.microfictionapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier of the like")
    private Long id;

    @Schema(description = "The ID of the user who liked the story")
    private Long userId;

    @Schema(description = "The ID of the story that was liked")
    private Long storyId;

    public Like() {
    }

    public Like(Long id, Long userId, Long storyId) {
        this.id = id;
        this.userId = userId;
        this.storyId = storyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
