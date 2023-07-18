package com.ryoshi.PopSauce.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    public Player(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Player(String username, int points) {
        this.username = username;
    }

    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
