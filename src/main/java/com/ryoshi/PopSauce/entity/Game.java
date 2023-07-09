package com.ryoshi.PopSauce.entity;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @OneToOne
    private Player host;
    @Value("current_timer")
    private int currentTimer;
    @OneToOne
    @Value("current_picture")
    private Pictures currentPicture;
    @OneToOne
    private Setting setting;
    @OneToMany
    private List<Player> players;
    private boolean started;
    @ManyToMany
    private List<Pictures> pictures;

    public Game(){

    }

    public Game(Long id, String code, Player host, int currentTimer, Pictures currentPicture, Setting setting, List<Player> players, boolean started) {
        this.id = id;
        this.code = code;
        this.host = host;
        this.currentTimer = currentTimer;
        this.currentPicture = currentPicture;
        this.setting = setting;
        this.players = players;
        this.started = started;
    }

    public Game(String code, Player host, int currentTimer, Pictures currentPicture, Setting setting, List<Player> players, boolean started) {
        this.code = code;
        this.host = host;
        this.currentTimer = currentTimer;
        this.currentPicture = currentPicture;
        this.setting = setting;
        this.players = players;
        this.started = started;
    }



    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public int getCurrentTimer() {
        return currentTimer;
    }

    public void setCurrentTimer(int currentTimer) {
        this.currentTimer = currentTimer;
    }

    public Pictures getCurrentPicture() {
        return currentPicture;
    }

    public void setCurrentPicture(Pictures currentPicture) {
        this.currentPicture = currentPicture;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
