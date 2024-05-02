package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.Role;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            user.setRole(Role.ROLE_USER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return user;
        }
        return null;
    }

    public User registerAdmin(User user) {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            user.setRole(Role.ROLE_ADMIN);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return user;
        }
        return null;
    }

    public boolean deleteUserById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public User findUserById(Long userId) {
        Optional<User> user = Optional.empty();
        if (userRepository.existsById(userId)) {
            user = userRepository.findById(userId);
        }
        return user.orElse(null);
    }

    public List<User> findAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User findUserByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            return userRepository.save(user);
        }
        return null;
    }
}
