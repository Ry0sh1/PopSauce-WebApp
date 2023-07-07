package com.ryoshi.PopSauce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
    @Value("actual_timer")
    private int actualTimer;
    @OneToOne
    @Value("actual_picture")
    private Pictures actualPicture;
    @OneToOne
    private Setting setting;

    public Game(){

    }

    public Game(Long id, String code, Player host, int actualTimer, Pictures actualPicture, Setting setting) {
        this.id = id;
        this.code = code;
        this.host = host;
        this.actualTimer = actualTimer;
        this.actualPicture = actualPicture;
        this.setting = setting;
    }

    public Game(String code, Player host, int actualTimer, Pictures actualPicture, Setting setting) {
        this.code = code;
        this.host = host;
        this.actualTimer = actualTimer;
        this.actualPicture = actualPicture;
        this.setting = setting;
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

    public int getActualTimer() {
        return actualTimer;
    }

    public void setActualTimer(int actualTimer) {
        this.actualTimer = actualTimer;
    }

    public Pictures getActualPicture() {
        return actualPicture;
    }

    public void setActualPicture(Pictures actualPicture) {
        this.actualPicture = actualPicture;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
