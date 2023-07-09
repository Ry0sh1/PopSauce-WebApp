package com.ryoshi.PopSauce.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private int points;
    @ManyToOne
    @Nullable
    private Game game;

    public Player(Long id, String username, int points) {
        this.id = id;
        this.username = username;
        this.points = points;
    }

    public Player(String username, int points) {
        this.username = username;
        this.points = points;
    }

    public Player() {
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
