package com.ryoshi.PopSauce.entity.PlayerToGame;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;

import java.io.Serializable;

public class PlayerToGameId implements Serializable {

    private Player players;
    private Game game;

    public PlayerToGameId(Player players, Game game) {
        this.players = players;
        this.game = game;
    }

    public PlayerToGameId(Player players) {
        this.players = players;
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
        return game;
    }

    public void setGames(Game game) {
        this.game = game;
    }
}
