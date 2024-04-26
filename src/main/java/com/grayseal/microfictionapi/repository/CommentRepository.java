package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
