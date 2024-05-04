package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByStoryId(Long storyId);
}
