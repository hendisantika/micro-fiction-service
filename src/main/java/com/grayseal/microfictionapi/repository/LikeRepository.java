package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Like;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LikeRepository extends CrudRepository<Like, Long> {
    List<Like> findByUserId(Long userId);
    List<Like> findByStoryId(Long storyId);
}
