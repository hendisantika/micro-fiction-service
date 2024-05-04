package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.Comment;
import com.grayseal.microfictionapi.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        if (comment != null) {
            commentRepository.save(comment);
            return comment;
        }
        return null;
    }

    public Comment findCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.orElse(null);
    }

    public List<Comment> findCommentsByStoryId(Long storyId) {
        return commentRepository.findByStoryId(storyId);
    }

    public void deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            commentRepository.deleteById(commentId);
        }
    }
}
