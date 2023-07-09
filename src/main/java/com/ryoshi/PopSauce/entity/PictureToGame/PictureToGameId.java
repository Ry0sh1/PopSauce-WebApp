package com.ryoshi.PopSauce.entity.PictureToGame;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Pictures;

import java.io.Serializable;

public class PictureToGameId implements Serializable {

    private Game games;
    private Pictures pictures;

    public PictureToGameId(Game games, Pictures pictures) {
        this.games = games;
        this.pictures = pictures;
    }

    public PictureToGameId() {
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

    public void setPictures(Pictures picture) {
        this.pictures = picture;
    }

}
