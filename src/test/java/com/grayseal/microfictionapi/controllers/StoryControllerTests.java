package com.grayseal.microfictionapi.controllers;

import com.grayseal.microfictionapi.controller.StoryController;
import com.grayseal.microfictionapi.service.StoryService;
import com.grayseal.microfictionapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoryControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private StoryService storyService;

    private StoryController storyController;

    @BeforeEach
    void setUp() {
        storyController = new StoryController(storyService, userService);
    }

    @Test
    @DirtiesContext
    void shouldCreateAStory() {
//        ResponseEntity<Story> response = restTemplate
//                .withBasicAuth("l@gmail.com", "password")
//                .postForEntity();
    }


}
