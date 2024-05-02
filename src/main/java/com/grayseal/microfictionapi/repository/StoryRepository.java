package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface StoryRepository extends CrudRepository<Story, Long> {
    Page<Story> findByUserId(Long userId, Pageable pageable);
    Page<Story> findAll(Pageable pageable);
}
