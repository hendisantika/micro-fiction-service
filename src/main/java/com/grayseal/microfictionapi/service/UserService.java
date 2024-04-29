package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.Role;
import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.model.UserRegistrationRequest;
import com.grayseal.microfictionapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(UserRegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()) == null) {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setRole(Role.ROLE_USER);
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean registerAdmin(UserRegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()) == null) {
            User admin = new User();
            admin.setEmail(registrationRequest.getEmail());
            admin.setRole(Role.ROLE_ADMIN);
            admin.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            userRepository.save(admin);
            return true;
        }
        return false;
    }

}
