package com.grayseal.microfictionapi.service;

import com.grayseal.microfictionapi.model.User;
import com.grayseal.microfictionapi.model.UserRegistrationRequest;
import com.grayseal.microfictionapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTests {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testCreateUser() {
        String email = "lynne@gmail.com";
        String password = "12345";
        UserRegistrationRequest request = new UserRegistrationRequest(email, password);

        // Mock the UserRepository behavior
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Mock the PasswordEncoder behavior
        String encodedPassword = "encodedPassword";
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        Long result = userService.registerUser(request);

        assertThat(result).isNotNull();
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class)); // Verify userRepository.save() is called once
    }
}
