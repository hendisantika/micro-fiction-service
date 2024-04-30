package com.grayseal.microfictionapi.repository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository

import com.grayseal.microfictionapi.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);

    List<User> findAll();
}