package com.grayseal.microfictionapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Schema(description = "The unique identifier of the user")
    private Long id;

    @Schema(description = "The email address of the user")
    private String email;

    @Schema(description = "The password of the user")
    private String password;

    //ROLE will be stored as String in DB
    @Enumerated(EnumType.STRING)
    @Schema(description = "The role of the user")
    private Role role;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User(Long id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {
    }
}
