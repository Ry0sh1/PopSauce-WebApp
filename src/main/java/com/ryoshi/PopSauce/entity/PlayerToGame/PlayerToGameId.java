package com.ryoshi.PopSauce.entity.PlayerToGame;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;

import java.io.Serializable;

public class PlayerToGameId implements Serializable {

    private Player players;
    private Game games;

    public PlayerToGameId(Player players, Game games) {
        this.players = players;
        this.games = games;
    }

    public PlayerToGameId() {
    }

    public Player getPlayers() {
        return players;
    }

    public void setPlayers(Player players) {
        this.players = players;
    }

    public Game getGames() {
        return games;
    }

    public void setGames(Game games) {
        this.games = games;
    }
}
