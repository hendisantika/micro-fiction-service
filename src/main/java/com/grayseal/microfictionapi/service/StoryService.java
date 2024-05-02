package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.Story;
import com.grayseal.microfictionapi.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<Story> findAllStories(Pageable pageable) {
        return storyRepository.findAll(pageable);
    }

    public boolean existsById(Long storyId) {
        return storyRepository.existsById(storyId);
    }

    public Story updateStory(Long storyId, Story updatedStory) {
        Optional<Story> optionalStory = storyRepository.findById(storyId);
        if (optionalStory.isPresent()) {
            Story story = optionalStory.get();
            story.setTitle(updatedStory.getTitle());
            story.setContent(updatedStory.getContent());
            story.setUserId(updatedStory.getUserId());
            story.setCreationDate(updatedStory.getCreationDate());
            return storyRepository.save(story);
        }
        return null;
    }

    public void deleteStory(Long storyId) {
        Optional<Story> optionalStory = storyRepository.findById(storyId);
        if (optionalStory.isPresent()) {
            storyRepository.deleteById(storyId);
        }
    }
}
