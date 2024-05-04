package com.grayseal.microfictionapi.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "MicroFiction API | 📚✍️",
                version = "1.0",
                description = "The Microfiction API is a RESTful web service designed for writers to create, publish, and explore microfiction stories. Microfiction, also known as flash fiction or short-short stories, is a genre of fiction characterized by its brevity, typically consisting of narratives under 300 words. This API provides writers with the tools to manage their stories, interact with other writers, and engage with readers in a vibrant community dedicated to the art of concise storytelling.\n\nKey Features:\n- User Management: Create accounts, manage profiles, and connect with fellow writers.\n- Story Creation and Publication: Write, edit, and publish microfiction stories with ease.\n- Social Interaction: Like, comment on, and share stories to engage with other writers and readers.\n- Search and Discovery: Explore a diverse collection of microfiction stories through advanced search and discovery features.\n- Security and Privacy: Secure authentication and authorization mechanisms protect user data and ensure privacy. The Microfiction API offers a platform for creativity, inspiration, and connection within the community."
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/**")
                .build();
    }
}