package com.grayseal.microfictionapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "stories")
public class Story {

    @Id
    @Schema(description = "The unique identifier of the story")
    private Long id;

    @Schema(description = "The title of the story")
    private String title;

    @Schema(description = "The content of the story")
    private String content;

    @Schema(description = "The ID of the user who created the story")
    private Long userId;

    @Schema(description = "The date when the story was created")
    private Date creationDate;

    public Story(Long id, String title, String content, Long userId, Date creationDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.creationDate = creationDate;
    }

    public Story() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
