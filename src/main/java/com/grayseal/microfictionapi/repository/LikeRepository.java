package com.grayseal.microfictionapi.repository;

import com.grayseal.microfictionapi.model.Like;
import org.springframework.data.repository.CrudRepository;

public interface LikeRepository extends CrudRepository<Like, Long> {
}
