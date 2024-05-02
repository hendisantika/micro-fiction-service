package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Story;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoryRepository extends CrudRepository<Story, Long> {
    List<Story> findAllByUserId(Long userId);
}
