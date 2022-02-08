package com.dumpster_sensor.webapp.models;

import javax.persistence.*;

@Entity
@Table(name = "user")

public class User {
    //Model in MVC.

    //PK. Autogenerated in DB
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 45)
    private String role;

    @Column(nullable = false, length = 45)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 45)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}