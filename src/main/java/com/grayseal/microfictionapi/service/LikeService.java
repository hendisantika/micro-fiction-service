package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.Like;
import com.grayseal.microfictionapi.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like createLike(Like like) {
        if (like != null) {
            likeRepository.save(like);
            return like;
        }
        return null;
    }

    public Like getLikeById(Long likeId) {
        Optional<Like> like = likeRepository.findById(likeId);
        return like.orElse(null);
    }

    public List<Like> getLikesByStoryId(Long storyId) {
        return likeRepository.findByStoryId(storyId);
    }

    public List<Like> getLikesByUserId(Long userId) {
        return likeRepository.findByUserId(userId);
    }

    public void deleteLike(Long likeId) {
        Optional<Like> like = likeRepository.findById(likeId);
        if (like.isPresent()) {
            likeRepository.deleteById(likeId);
        }
    }
}
