package com.grayseal.microfictionapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "stories")
public class Story {

    @Id
    private Long id;

    private String title;

    private String content;

    public Story(Long id, String title, String content, Long userId, Date creationDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.creationDate = creationDate;
    }

    public Story() {
    }

    private Long userId;

    private Date creationDate;

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
