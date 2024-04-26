# Microfiction API (Under Development) 🚧

The Microfiction API is a RESTful web service designed for writers to create, publish, and explore microfiction stories. Microfiction, also known as flash fiction or short-short stories, is a genre of fiction characterized by its brevity, typically consisting of narratives under 300 words.

## Key Features

- **User Management**: Create accounts, manage profiles, and connect with fellow writers.
- **Story Creation and Publication**: Write, edit, and publish microfiction stories with ease.
- **Social Interaction**: Like, comment on, and share stories to engage with other writers and readers.
- **Search and Discovery**: Explore a diverse collection of microfiction stories through advanced search and discovery features.
- **Security and Privacy**: Secure authentication and authorization mechanisms protect user data and ensure privacy.

## UML Class Diagram
The UML class diagram below illustrates the core entities of the service.
<img src="uml.png"/>

## Installation

1. Clone the repository: `git clone https://github.com/your_username/microfiction-api.git`
2. Navigate to the project directory: `cd microfiction-api`
3. Build the project: `mvn clean install`
4. Run the application: `mvn spring-boot:run`

## API Documentation

The API documentation is generated using Springdoc OpenAPI. After running the application, you can access the Swagger UI at `http://localhost:8080/swagger-ui.html`.

## Dependencies

- Spring Boot
- Spring Data JPA
- Spring Security
- Springdoc OpenAPI
- MySQL

## Configuration

The application configuration can be found in the `application.properties` file. You can customize database settings, logging, and other properties in this file.