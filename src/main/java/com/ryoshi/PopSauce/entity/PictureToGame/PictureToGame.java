package com.ryoshi.PopSauce.entity.PictureToGame;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Pictures;
import jakarta.persistence.*;

@Entity
@IdClass(PictureToGameId.class)
@Table(name = "pictures_games")
public class PictureToGame {

    @Id
    @OneToOne
    private Game games;
    @Id
    @OneToOne
    private Pictures pictures;
    private int place;

    public PictureToGame(Game games, Pictures pictures, int place) {
        this.games = games;
        this.pictures = pictures;
        this.place = place;
    }

    public PictureToGame(Pictures pictures, int place) {
        this.pictures = pictures;
        this.place = place;
    }

    public PictureToGame() {
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public Game getGames() {
        return games;
    }

    public void setGames(Game games) {
        this.games = games;
    }

    public Pictures getPictures() {
        return pictures;
    }

    public void setPictures(Pictures pictures) {
        this.pictures = pictures;
    }
}
