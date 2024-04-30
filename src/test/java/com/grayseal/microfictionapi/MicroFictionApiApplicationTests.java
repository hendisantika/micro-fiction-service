package com.grayseal.microfictionapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroFictionApiApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
}
