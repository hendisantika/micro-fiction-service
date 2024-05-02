package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.Story;
import com.grayseal.microfictionapi.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoryService {

    private final StoryRepository storyRepository;

    @Autowired
    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public Story createStory(Story story) {
        if (story != null) {
            storyRepository.save(story);
            return story;
        }
        return null;
    }

    public Story findStoryById(Long storyId) {
        Optional<Story> optionalStory = storyRepository.findById(storyId);
        return optionalStory.orElse(null);
    }

    public Page<Story> findAllStoriesByUser(Long userId, Pageable pageable) {
        Page<Story> page = storyRepository.findByUserId(userId, pageable);
        return page;
    }

    public List<Story> findAllStories() {
        return (List<Story>) storyRepository.findAll();
    }

    public boolean existsById(Long storyId) {
        return storyRepository.existsById(storyId);
    }

    public Story updateStory(Long storyId, String title, String content) {
        Optional<Story> optionalStory = storyRepository.findById(storyId);
        if (optionalStory.isPresent()) {
            Story story = optionalStory.get();
            story.setTitle(title);
            story.setContent(content);
            return storyRepository.save(story);
        }
        return null;
    }

    public boolean deleteStory(Long storyId) {
        Optional<Story> optionalStory = storyRepository.findById(storyId);
        if (optionalStory.isPresent()) {
            storyRepository.deleteById(storyId);
            return true;
        }
        return false;
    }
}
