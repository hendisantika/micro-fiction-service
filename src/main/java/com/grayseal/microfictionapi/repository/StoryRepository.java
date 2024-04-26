package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Story;
import org.springframework.data.repository.CrudRepository;

public interface StoryRepository extends CrudRepository<Story, Long> {
}
